package com.ryan_mtg.servobot.discord.events;

import com.ryan_mtg.servobot.discord.bot.BotHome;
import net.dv8tion.jda.api.events.message.guild.GenericGuildMessageEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.user.UserActivityStartEvent;
import net.dv8tion.jda.api.events.user.update.GenericUserPresenceEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateOnlineStatusEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class HomeDelegatingListener extends ListenerAdapter {
    private static Logger LOGGER = LoggerFactory.getLogger(HomeDelegatingListener.class);
    private Map<String, ListenerAdapter> botHomeMap = new HashMap<>();

    public void register(final BotHome botHome) {
        botHomeMap.put(botHome.getHomeName(), botHome.getListener());
    }

    @Override
    public void onGuildMessageReceived(final @Nonnull GuildMessageReceivedEvent event) {
        ListenerAdapter listenerAdapter = getListener(event);
        if (listenerAdapter != null) {
            listenerAdapter.onGuildMessageReceived(event);
        }
    }

    @Override
    public void onUserActivityStart(@Nonnull UserActivityStartEvent event) {
        ListenerAdapter listenerAdapter = getListener(event);
        if (listenerAdapter != null) {
            listenerAdapter.onUserActivityStart(event);
        }
    }

    @Override
    public void onUserUpdateOnlineStatus(@Nonnull UserUpdateOnlineStatusEvent event) {
        ListenerAdapter listenerAdapter = getListener(event);
        if (listenerAdapter != null) {
            listenerAdapter.onUserUpdateOnlineStatus(event);
        }
    }

    private ListenerAdapter getListener(final GenericGuildMessageEvent event) {
        String homeName = event.getGuild().getName();
        LOGGER.trace("HomeDelegatingListener received " + event + " for " + homeName);
        return getListener(homeName);
    }

    private ListenerAdapter getListener(final GenericUserPresenceEvent event) {
        String homeName = event.getGuild().getName();
        LOGGER.trace("HomeDelegatingListener received " + event + " for " + homeName);
        return getListener(homeName);
    }

    private ListenerAdapter getListener(final String homeName) {
        ListenerAdapter listener = botHomeMap.get(homeName);
        if (listener == null) {
            LOGGER.warn("No listener found for '" + homeName + "'");
            for (String home : botHomeMap.keySet()) {
                LOGGER.warn("  possible homes: " + home);
            }
        }
        return listener;
    }
}
