package com.ryan_mtg.servobot.discord.model;

import com.ryan_mtg.servobot.data.factories.UserSerializer;
import com.ryan_mtg.servobot.data.models.ServiceRow;
import com.ryan_mtg.servobot.discord.event.DiscordEventAdapter;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.EventListener;
import com.ryan_mtg.servobot.model.BotHome;
import com.ryan_mtg.servobot.model.Home;
import com.ryan_mtg.servobot.model.HomeEditor;
import com.ryan_mtg.servobot.model.Service;
import com.ryan_mtg.servobot.model.ServiceHome;
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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DiscordService implements Service {
    public static final int TYPE = 2;

    private String token;
    private JDA jda;
    private UserSerializer userSerializer;

    private Map<Long, Integer> homeIdMap = new HashMap<>();

    public DiscordService(final String token, final UserSerializer userSerializer) throws BotErrorException {
        this.token = token;
        this.userSerializer = userSerializer;

        if (token.length() > MAX_TOKEN_SIZE) {
            throw new BotErrorException(String.format("Token too long (max %d): %s", MAX_TOKEN_SIZE, token));
        }
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
    public void register(final BotHome botHome) {
        ServiceHome serviceHome = botHome.getServiceHome(DiscordService.TYPE);
        if (serviceHome != null) {
            homeIdMap.put(((DiscordServiceHome) serviceHome).getGuildId(), botHome.getId());
        }
    }

    @Override
    public void unregister(final BotHome botHome) {
        ServiceHome serviceHome = botHome.getServiceHome(DiscordService.TYPE);
        if (serviceHome != null) {
            homeIdMap.remove(((DiscordServiceHome) serviceHome).getGuildId());
        }
    }

    @Override
    public void start(final EventListener eventListener) throws Exception {
        JDABuilder builder = new JDABuilder(token);
        builder.setActivity(Activity.playing("Beta: " + now()));

        builder.addEventListeners(new DiscordEventAdapter(eventListener, homeIdMap, userSerializer));
        jda = builder.build();
        jda.awaitReady();
    }

    @Override
    public void whisper(final com.ryan_mtg.servobot.user.User user, final String message) {
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

    public List<String> getEmotes(final long guildId) {
        Guild guild = jda.getGuildById(guildId);
        return guild.getEmotes().stream().map(emote -> emote.getName()).collect(Collectors.toList());
    }

    public List<String> getRoles(final long guildId) {
        Guild guild = jda.getGuildById(guildId);
        return guild.getRoles().stream().filter(role -> !role.isManaged() && !role.isPublicRole())
                .map(role -> role.getName()).collect(Collectors.toList());
    }
}
