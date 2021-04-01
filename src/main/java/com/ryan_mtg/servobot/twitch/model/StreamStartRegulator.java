package com.ryan_mtg.servobot.twitch.model;

import com.github.twitch4j.TwitchClient;
import com.ryan_mtg.servobot.events.EventListener;
import com.ryan_mtg.servobot.model.BotHome;
import com.ryan_mtg.servobot.twitch.event.TwitchStreamStartEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class StreamStartRegulator implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(StreamStartRegulator.class);

    private final TwitchService twitchService;
    private TwitchClient client;
    private EventListener eventListener;
    private final Map<Long, BotHome> homeMap;
    private final Map<Long, Boolean> isStreamingMap = new HashMap<>();
    private final Map<Long, Instant> previousStartMap = new HashMap<>();

    public StreamStartRegulator(final TwitchService twitchService, final Map<Long, BotHome> homeMap) {
        this.twitchService = twitchService;
        this.homeMap = homeMap;
    }

    public void start(final TwitchClient client, final EventListener eventListener) {
        this.client = client;
        this.eventListener = eventListener;

        for (Map.Entry<Long, BotHome> entry : homeMap.entrySet()) {
            long channelId = entry.getKey();
            boolean isStreaming = entry.getValue().getServiceHome(TwitchService.TYPE).isStreaming();
            synchronized (this) {
                isStreamingMap.put(channelId, isStreaming);
            }
        }
    }

    public void addHome(final TwitchServiceHome serviceHome) {
        if (hasStarted()) {
            long channelId = serviceHome.getChannelId();
            boolean isStreaming = serviceHome.isStreaming();
            synchronized (this) {
                isStreamingMap.put(channelId, isStreaming);
            }
        }
    }

    public void removeHome(final TwitchServiceHome serviceHome) {
        long channelId = serviceHome.getChannelId();
        synchronized (this) {
            isStreamingMap.remove(channelId);
        }
    }

    @Override
    public void run() {
        try {
            List<Long> allIds;
            synchronized (this) {
                allIds = new ArrayList<>(isStreamingMap.keySet());
            }

            Set<Long> streamingIds = twitchService.getChannelsStreaming(allIds);

            LOGGER.trace("Checking streams for starting");
            for (long id : allIds) {
                boolean isStreaming = streamingIds.contains(id);
                boolean wasStreaming;
                synchronized (this) {
                    if (!isStreamingMap.containsKey(id)) {
                        continue;
                    }
                    wasStreaming = isStreamingMap.get(id);
                    isStreamingMap.put(id, isStreaming);
                }
                LOGGER.trace("  > stream {} wasStreaming: {}, isStreaming: {}", id, wasStreaming, isStreaming);

                if (!wasStreaming && isStreaming) {
                    BotHome home = homeMap.get(id);
                    String channelName = home.getServiceHome(TwitchService.TYPE).getName();
                    Instant previousStart = previousStartMap.get(id);
                    Instant now = Instant.now();
                    if (previousStart == null || Duration.between(previousStart, now).toMinutes() > 20) {
                        LOGGER.info("Stream is starting for {}", channelName);
                        long channelId = ((TwitchServiceHome) home.getServiceHome(TwitchService.TYPE)).getChannelId();
                        eventListener.onStreamStart(
                                new TwitchStreamStartEvent(client, homeMap.get(id), channelName, channelId));
                        previousStartMap.put(id, now);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Caught exception while regulating stream starts: ", e);
            e.printStackTrace();
        }
    }

    private boolean hasStarted() {
        return client != null;
    }
}
