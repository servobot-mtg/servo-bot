package com.ryan_mtg.servobot.events;

import com.ryan_mtg.servobot.model.BotEditor;
import com.ryan_mtg.servobot.model.BotHome;
import com.ryan_mtg.servobot.model.HomeEditor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class HomeDelegatingListener implements EventListener {
    private static Logger LOGGER = LoggerFactory.getLogger(HomeDelegatingListener.class);

    private BotEditor botEditor;
    private Map<Integer, HomeEditor> homeEditorMap;
    private Map<Integer, EventListener> botHomeMap = new HashMap<>();

    public HomeDelegatingListener(final BotEditor botEditor, final Map<Integer, HomeEditor> homeEditorMap) {
        this.botEditor = botEditor;
        this.homeEditorMap = homeEditorMap;
    }

    public void register(final BotHome botHome) {
        botHomeMap.put(botHome.getId(), botHome.getEventListener());
    }

    public void unregister(BotHome botHome) {
        botHomeMap.remove(botHome.getId());
    }

    @Override
    public void onMessage(final MessageHomeEvent messageHomeEvent) throws BotErrorException {
        EventListener listener = getListener(messageHomeEvent);
        if (listener != null) {
            listener.onMessage(messageHomeEvent);
        }
    }

    @Override
    public void onStreamStart(final StreamStartEvent streamStartEvent) {
        EventListener listener = getListener(streamStartEvent);
        if (listener != null) {
            listener.onStreamStart(streamStartEvent);
        }
    }

    @Override
    public void onNewUser(final UserHomeEvent newUserEvent) throws BotErrorException {
        EventListener listener = getListener(newUserEvent);
        if (listener != null) {
            listener.onNewUser(newUserEvent);
        }
    }

    @Override
    public void onRaid(final UserHomeEvent raidEvent) throws BotErrorException {
        EventListener listener = getListener(raidEvent);
        if (listener != null) {
            listener.onRaid(raidEvent);
        }
    }

    @Override
    public void onSubscribe(final UserHomeEvent subscribeEvent) throws BotErrorException {
        EventListener listener = getListener(subscribeEvent);
        if (listener != null) {
            listener.onRaid(subscribeEvent);
        }
    }

    @Override
    public void onAlert(final AlertEvent alertEvent) {
        EventListener listener = getListener(alertEvent);
        if (listener != null) {
            listener.onAlert(alertEvent);
        }
    }

    private EventListener getListener(final BotHomeEvent event) {
        int homeId = event.getHomeId();
        event.setBotEditor(botEditor);
        event.setHomeEditor(homeEditorMap.get(homeId));
        return botHomeMap.get(homeId);
    }
}
