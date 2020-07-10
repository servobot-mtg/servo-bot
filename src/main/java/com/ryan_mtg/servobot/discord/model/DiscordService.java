package com.ryan_mtg.servobot.discord.model;

import com.ryan_mtg.servobot.data.factories.LoggedMessageSerializer;
import com.ryan_mtg.servobot.discord.event.DiscordEventAdapter;
import com.ryan_mtg.servobot.discord.event.StreamStartRegulator;
import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.events.EventListener;
import com.ryan_mtg.servobot.model.BotHome;
import com.ryan_mtg.servobot.model.Channel;
import com.ryan_mtg.servobot.model.Home;
import com.ryan_mtg.servobot.model.HomeEditor;
import com.ryan_mtg.servobot.model.Service;
import com.ryan_mtg.servobot.model.ServiceHome;
import com.ryan_mtg.servobot.user.UserTable;
import com.ryan_mtg.servobot.utility.Validation;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DiscordService implements Service {
    public static final int TYPE = 2;

    private JDA jda;
    private final String token;

    // guildId -> home
    private final Map<Long, BotHome> homeMap = new HashMap<>();
    private final UserTable userTable;
    private final LoggedMessageSerializer loggedMessageSerializer;
    private StreamStartRegulator streamStartRegulator = new StreamStartRegulator();

    //TODO: put this in DiscordHome once it's merged with ServiceHome
    private Map<Long, List<com.ryan_mtg.servobot.model.Emote>> emoteCache = new HashMap<>();

    public DiscordService(final String token, final UserTable userTable,
            final LoggedMessageSerializer loggedMessageSerializer) throws UserError {
        this.token = token;
        this.userTable = userTable;
        this.loggedMessageSerializer = loggedMessageSerializer;

        Validation.validateStringLength(token, Validation.MAX_AUTHENTICATION_TOKEN_LENGTH, "Token");
    }

    @Override
    public int getType() {
        return TYPE;
    }

    @Override
    public String getName() {
        return "Discord";
    }

    @Override
    public String getBotName() {
        return jda.getSelfUser().getName();
    }

    @Override
    public String getImageUrl() {
        return null;
    }

    @Override
    public void register(final BotHome botHome) {
        ServiceHome serviceHome = botHome.getServiceHome(DiscordService.TYPE);
        if (serviceHome != null) {
            DiscordServiceHome discordServiceHome = (DiscordServiceHome) serviceHome;
            long guildId = discordServiceHome.getGuildId();
            streamStartRegulator.addHome(botHome, computeIsStreaming(guildId));
            homeMap.put(guildId, botHome);
        }
    }

    @Override
    public void unregister(final BotHome botHome) {
        ServiceHome serviceHome = botHome.getServiceHome(DiscordService.TYPE);
        if (serviceHome != null) {
            DiscordServiceHome discordServiceHome = (DiscordServiceHome) serviceHome;
            streamStartRegulator.removeHome(botHome);
            homeMap.remove(discordServiceHome.getGuildId());
        }
    }

    @Override
    public void start(final EventListener eventListener) throws Exception {
        JDABuilder builder = new JDABuilder(token);
        builder.setActivity(Activity.playing("Beta: " + now()));

        builder.addEventListeners(new DiscordEventAdapter(this, eventListener, homeMap,
                streamStartRegulator, userTable, loggedMessageSerializer));
        jda = builder.build();
        jda.awaitReady();
        homeMap.forEach(
                (guildId, home) -> streamStartRegulator.setIsStreaming(home.getId(), computeIsStreaming(guildId)));
    }

    public void logSendMessage(final com.ryan_mtg.servobot.user.User user, final String message) {
        loggedMessageSerializer.logSentMessage(user, message, DiscordService.TYPE);
    }

    @Override
    public void whisper(final com.ryan_mtg.servobot.user.User user, final String message) {
        logSendMessage(user, message);
        User discordUser = jda.getUserById(user.getDiscordId());
        discordUser.openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(message).queue());
    }

    public Home getHome(final long guildId, final HomeEditor homeEditor) {
        Guild guild = jda.getGuildById(guildId);
        return new DiscordHome(guild, homeEditor);
    }

    public void setNickName(final long guildId, final String name) {
        Guild guild = jda.getGuildById(guildId);
        Member self = guild.getSelfMember();
        if (!name.equals(self.getNickname())) {
            guild.modifyNickname(guild.getSelfMember(), name).queue();
        }
    }

    private static String now() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("America/Chicago"));
        return formatter.format(now);
    }

    public List<com.ryan_mtg.servobot.model.Emote> getEmotes(final long guildId) {
        if (emoteCache.containsKey(guildId)) {
            return emoteCache.get(guildId);
        }
        updateEmotes(guildId);
        return emoteCache.get(guildId);
    }

    public void updateEmotes(final long guildId) {
        Guild guild = jda.getGuildById(guildId);

        List<Emote> emotes = guild.getEmotes();
        emoteCache.put(guildId, emotes.stream().map(emote -> new DiscordEmote(emote)).collect(Collectors.toList()));
    }

    public List<String> getRoles(final long guildId) {
        Guild guild = jda.getGuildById(guildId);
        return guild.getRoles().stream().filter(role -> !role.isManaged() && !role.isPublicRole())
                .map(Role::getName).collect(Collectors.toList());
    }

    public List<String> getChannels(final long guildId) {
        Guild guild = jda.getGuildById(guildId);
        return guild.getTextChannels().stream().map(GuildChannel::getName).collect(Collectors.toList());
    }

    public Channel getChannel(final long guildId, final String channelName) throws UserError {
        Guild guild = jda.getGuildById(guildId);

        TextChannel textChannel = guild.getTextChannels().stream().filter(channel -> channel.getName()
                .equals(channelName)).findFirst()
                .orElseThrow(() -> new UserError("No channel with name %s.", channelName));
        return new DiscordChannel(null, textChannel);
    }

    public boolean isStreaming(final long guildId) {
        return streamStartRegulator.isStreaming(homeMap.get(guildId).getId());
    }

    private boolean computeIsStreaming(final long guildId) {
        if (jda == null) {
            return false;
        }
        Guild guild = jda.getGuildById(guildId);
        Member owner = guild.getOwner();
        for(Activity activity : owner.getActivities()) {
            if (activity.getType() == Activity.ActivityType.STREAMING) {
                return true;
            }
        }
        return false;
    }
}
