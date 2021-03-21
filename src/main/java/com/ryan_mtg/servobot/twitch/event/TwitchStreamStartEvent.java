package com.ryan_mtg.servobot.twitch.event;

import com.github.twitch4j.TwitchClient;
import com.ryan_mtg.servobot.events.StreamStartEvent;
import com.ryan_mtg.servobot.model.BotHome;
import com.ryan_mtg.servobot.twitch.model.TwitchService;

public class TwitchStreamStartEvent extends TwitchHomeEvent implements StreamStartEvent {
    public TwitchStreamStartEvent(final TwitchClient twitchClient, final BotHome botHome, final String channelName,
            final long channelId) {
        super(twitchClient, botHome, channelName, channelId);
    }

    @Override
    public int getServiceType() {
        return TwitchService.TYPE;
    }
}
