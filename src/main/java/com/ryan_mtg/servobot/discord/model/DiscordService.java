package com.ryan_mtg.servobot.discord.model;

import com.ryan_mtg.servobot.data.factories.LoggedMessageSerializer;
import com.ryan_mtg.servobot.discord.event.DiscordEventAdapter;
import com.ryan_mtg.servobot.discord.event.StreamStartRegulator;
import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.events.EventListener;
import com.ryan_mtg.servobot.model.BotHome;
import com.ryan_mtg.servobot.model.Service;
import com.ryan_mtg.servobot.model.ServiceHome;
import com.ryan_mtg.servobot.user.UserTable;
import com.ryan_mtg.servobot.utility.Validation;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class DiscordService implements Service {
    public static final int TYPE = 2;

    private JDA jda;
    private final String token;

    // guildId -> home
    private final Map<Long, BotHome> homeMap = new HashMap<>();
    private final UserTable userTable;
    private final LoggedMessageSerializer loggedMessageSerializer;
    private StreamStartRegulator streamStartRegulator = new StreamStartRegulator();

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

    public Guild getGuild(final long guildId) {
        return jda.getGuildById(guildId);
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

    private static String now() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("America/Chicago"));
        return formatter.format(now);
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
