package com.ryan_mtg.servobot.events;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
public class MultiDelegatingListener implements EventListener {
    private final List<EventListener> listeners = new ArrayList<>();
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
    public void onPrivateMessage(final MessageEvent messageEvent) {}

    @Override
    public void onMessage(final MessageHomeEvent messageHomeEvent) {
        for (EventListener listener : getListeners()) {
            listener.onMessage(messageHomeEvent);
        }
    }

    @Override
    public void onEmoteAdded(final EmoteHomeEvent emoteHomeEvent) {
        for (EventListener listener : getListeners()) {
            listener.onEmoteAdded(emoteHomeEvent);
        }
    }

    @Override
    public void onEmoteRemoved(final EmoteHomeEvent emoteHomeEvent) {
        for (EventListener listener : getListeners()) {
            listener.onEmoteRemoved(emoteHomeEvent);
        }
    }

    @Override
    public void onStreamStart(final StreamStartEvent streamStartEvent) {
        for (EventListener listener : getListeners()) {
            listener.onStreamStart(streamStartEvent);
        }
    }

    @Override
    public void onNewUser(final UserHomeEvent newUserEvent) {
        for (EventListener listener : getListeners()) {
            listener.onNewUser(newUserEvent);
        }
    }

    @Override
    public void onRaid(final UserHomeEvent raidEvent) {
        for (EventListener listener : getListeners()) {
            listener.onRaid(raidEvent);
        }
    }

    @Override
    public void onSubscribe(final UserHomeEvent subscribeEvent) {
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
