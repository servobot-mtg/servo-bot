package com.ryan_mtg.servobot.twitch.model;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.helix.domain.StreamList;
import com.ryan_mtg.servobot.events.EventListener;
import com.ryan_mtg.servobot.model.BotHome;
import com.ryan_mtg.servobot.twitch.event.TwitchStreamStartEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class StreamStartRegulator implements Runnable {
    private static Logger LOGGER = LoggerFactory.getLogger(StreamStartRegulator.class);

    private TwitchClient client;
    private EventListener eventListener;
    private Map<Long, BotHome> homeMap;
    private Map<Long, Boolean> isStreamingMap = new HashMap<>();

    public StreamStartRegulator(final Map<Long, BotHome> homeMap) {
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
        List<Long> allIds;
        synchronized (this) {
            allIds = new ArrayList<>(isStreamingMap.keySet());
        }
        List<String> channelIds = allIds.stream().map(id -> Long.toString(id)).collect(Collectors.toList());
        StreamList streamList = client.getHelix().getStreams(null, "", null, null,null, null, null,
                channelIds, null).execute();

        Set<Long> streamingIds = new HashSet<>();
        Map<Long, String> channelNameMap = new HashMap<>();
        streamList.getStreams().forEach(stream -> {
            long id = Long.parseLong(stream.getId());
            streamingIds.add(id);
            channelNameMap.put(id, stream.getUserId());
        });

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

            if (!wasStreaming && isStreaming) {
                LOGGER.info("Stream is starting for {}", channelNameMap.get(id));
                eventListener.onStreamStart(
                        new TwitchStreamStartEvent(client, homeMap.get(id).getId(), channelNameMap.get(id)));
            }
        }
    }

    private boolean hasStarted() {
        return client != null;
    }
}
