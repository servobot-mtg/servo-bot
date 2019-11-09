package com.ryan_mtg.servobot.events;

import com.ryan_mtg.servobot.model.Home;
import com.ryan_mtg.servobot.model.Message;

import java.util.ArrayList;
import java.util.List;

public class MultiDelegatingListener implements EventListener {
    private List<EventListener> listeners = new ArrayList<>();

    public MultiDelegatingListener(final EventListener... listeners) {
        for (EventListener listener : listeners) {
            add(listener);
        }
    }

    public void add(final EventListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void onMessage(final MessageSentEvent messageSentEvent) {
        for (EventListener listener : listeners) {
            listener.onMessage(messageSentEvent);
        }
    }

    @Override
    public void onStreamStart(final StreamStartEvent streamStartEvent) {
        for (EventListener listener : listeners) {
            listener.onStreamStart(streamStartEvent);
        }
    }
}
