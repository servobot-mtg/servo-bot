package com.ryan_mtg.servobot.discord.bot;

import com.ryan_mtg.servobot.discord.events.HomeDelegatingListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

import javax.security.auth.login.LoginException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Bot {
    private List<BotHome> homes = new ArrayList<>();
    private HomeDelegatingListener listener = new HomeDelegatingListener();
    private String token;

    public Bot(final String token) {
        this.token = token;
    }

    public void addHome(final BotHome home) {
        homes.add(home);
        listener.register(home);
    }

    public void startBot() throws LoginException {
        JDABuilder builder = new JDABuilder(token);
        builder.setActivity(Activity.playing("Beta: " + now()));

        builder.addEventListeners(listener);
        JDA jda = builder.build();
    }

    public List<BotHome> getHomes() {
        return homes;
    }

    public BotHome getHome(final String homeName) {
        for(BotHome home : homes) {
            if (home.getHomeName().equals(homeName)) {
                return home;
            }
        }
        return null;
    }

    private static String now() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalDateTime now = LocalDateTime.now();
        return formatter.format(now);
    }
}
