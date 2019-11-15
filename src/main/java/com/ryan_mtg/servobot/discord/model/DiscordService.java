package com.ryan_mtg.servobot.discord.model;

import com.ryan_mtg.servobot.discord.event.DiscordEventAdapter;
import com.ryan_mtg.servobot.events.EventListener;
import com.ryan_mtg.servobot.model.BotHome;
import com.ryan_mtg.servobot.model.Home;
import com.ryan_mtg.servobot.model.Service;
import com.ryan_mtg.servobot.model.ServiceHome;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class DiscordService implements Service {
    public static final int TYPE = 2;
    private String token;
    private JDA jda;

    private Map<Long, Integer> homeIdMap = new HashMap<>();

    public DiscordService(final String token) {
        this.token = token;
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
    public void start(final EventListener eventListener) throws Exception {
        JDABuilder builder = new JDABuilder(token);
        builder.setActivity(Activity.playing("Beta: " + now()));

        builder.addEventListeners(new DiscordEventAdapter(eventListener, homeIdMap));
        jda = builder.build();
    }

    public Home getHome(final long guildId) {
        Guild guild = jda.getGuildById(guildId);
        return new DiscordHome(guild);
    }

    private static String now() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalDateTime now = LocalDateTime.now();
        return formatter.format(now);
    }
}
