package com.ryan_mtg.servobot.events;

import com.ryan_mtg.servobot.model.BotHome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class HomeDelegatingListener implements EventListener {
    private static Logger LOGGER = LoggerFactory.getLogger(HomeDelegatingListener.class);
    private Map<Integer, EventListener> botHomeMap = new HashMap<>();

    public void register(final BotHome botHome) {
        botHomeMap.put(botHome.getId(), botHome.getListener());
    }

    @Override
    public void onMessage(final MessageSentEvent messageSentEvent) {
        EventListener listener = getListener(messageSentEvent.getHomeId());
        if (listener != null) {
            listener.onMessage(messageSentEvent);
        }
    }

    @Override
    public void onStreamStart(final StreamStartEvent streamStartEvent) {
        EventListener listener = getListener(streamStartEvent.getHomeId());
        if (listener != null) {
            listener.onStreamStart(streamStartEvent);
        }
    }

    private EventListener getListener(final int homeId) {
        return botHomeMap.get(homeId);
    }
}
