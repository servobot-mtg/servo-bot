package com.ryan_mtg.servobot.events;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MultiDelegatingListener implements EventListener {
    private List<EventListener> listeners = new ArrayList<>();
    private boolean active = false;

    public MultiDelegatingListener(final EventListener... listeners) {
        for (EventListener listener : listeners) {
            add(listener);
        }
    }

    public void add(final EventListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void onMessage(final MessageHomeEvent messageHomeEvent) {
        try {
            for (EventListener listener : getListeners()) {
                listener.onMessage(messageHomeEvent);
            }
        } catch (BotErrorException e) {
            messageHomeEvent.getMessage().getChannel().say(e.getErrorMessage());
        }
    }

    @Override
    public void onStreamStart(final StreamStartEvent streamStartEvent) {
        for (EventListener listener : getListeners()) {
            listener.onStreamStart(streamStartEvent);
        }
    }

    @Override
    public void onNewUser(final UserHomeEvent newUserEvent) throws BotErrorException {
        for (EventListener listener : getListeners()) {
            listener.onNewUser(newUserEvent);
        }
    }

    @Override
    public void onRaid(final UserHomeEvent raidEvent) throws BotErrorException {
        for (EventListener listener : getListeners()) {
            listener.onRaid(raidEvent);
        }
    }

    @Override
    public void onSubscribe(final UserHomeEvent subscribeEvent) throws BotErrorException {
        for (EventListener listener : getListeners()) {
            listener.onSubscribe(subscribeEvent);
        }
    }

    @Override
    public void onAlert(final AlertEvent alertEvent) {
        for (EventListener listener : getListeners()) {
            listener.onAlert(alertEvent);
        }
    }

    public void setActive(final boolean active) {
        this.active = active;
    }

    private List<EventListener> getListeners() {
        if (active) {
            return listeners;
        }
        return Collections.emptyList();
    }
}
