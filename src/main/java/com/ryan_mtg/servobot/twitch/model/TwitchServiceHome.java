package com.ryan_mtg.servobot.twitch.model;

import com.github.twitch4j.TwitchClient;
import com.ryan_mtg.servobot.model.ServiceHome;

public class TwitchServiceHome implements ServiceHome {
    private long channelId;

    public TwitchServiceHome(final long channelId) {
        this.channelId = channelId;
    }

    @Override
    public int getServiceType() {
        return TwitchService.TYPE;
    }

    public long getChannelId() {
        return channelId;
    }
}
