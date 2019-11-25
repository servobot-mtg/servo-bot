package com.ryan_mtg.servobot.events;

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
        try {
            for (EventListener listener : listeners) {
                listener.onMessage(messageSentEvent);
            }
        } catch (BotErrorException e) {
            messageSentEvent.getMessage().getChannel().say(e.getErrorMessage());
        }
    }

    @Override
    public void onStreamStart(final StreamStartEvent streamStartEvent) {
        for (EventListener listener : listeners) {
            listener.onStreamStart(streamStartEvent);
        }
    }

    @Override
    public void onAlert(final AlertEvent alertEvent) {
        for (EventListener listener : listeners) {
            listener.onAlert(alertEvent);
        }
    }
}
