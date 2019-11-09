package com.ryan_mtg.servobot.twitch.event;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.ryan_mtg.servobot.events.EventListener;

import java.util.Map;

public class TwitchEventGenerator {
    private EventListener eventListener;
    private Map<Long, Integer> homeIdMap;

    public TwitchEventGenerator(final TwitchClient client, final EventListener eventListener, final Map<Long, Integer> homeIdMap) {
        this.eventListener = eventListener;
        this.homeIdMap = homeIdMap;

        client.getEventManager().onEvent(ChannelMessageEvent.class).subscribe(event -> {
            eventListener.onMessage(new TwitchMessageSentEvent(event, resolveBotHomeId(event.getChannel().getId())));
        });
    }

    private int resolveBotHomeId(final String channelId) {
        return homeIdMap.get(Long.parseLong(channelId));
    }
}
