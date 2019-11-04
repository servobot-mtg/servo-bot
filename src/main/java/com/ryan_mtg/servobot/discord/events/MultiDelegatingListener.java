package com.ryan_mtg.servobot.discord.events;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.user.UserActivityStartEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateOnlineStatusEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class MultiDelegatingListener extends ListenerAdapter {
    private static Logger LOGGER = LoggerFactory.getLogger(MultiDelegatingListener.class);
    private List<ListenerAdapter> listeners = new ArrayList<>();

    public MultiDelegatingListener(final ListenerAdapter... listeners) {
        for (ListenerAdapter listener : listeners) {
            add(listener);
        }
    }

    public void add(final ListenerAdapter listener) {
        this.listeners.add(listener);
    }

    @Override
    public void onGuildMessageReceived(final @Nonnull GuildMessageReceivedEvent event) {
        for (ListenerAdapter listener : listeners) {
            listener.onGuildMessageReceived(event);
        }
    }

    @Override
    public void onUserActivityStart(@Nonnull UserActivityStartEvent event) {
        LOGGER.trace("activity start");
        for (ListenerAdapter listener : listeners) {
            listener.onUserActivityStart(event);
        }
    }

    @Override
    public void onUserUpdateOnlineStatus(@Nonnull UserUpdateOnlineStatusEvent event) {
        LOGGER.trace("online status");
        for (ListenerAdapter listener : listeners) {
            listener.onUserUpdateOnlineStatus(event);
        }
    }
}
