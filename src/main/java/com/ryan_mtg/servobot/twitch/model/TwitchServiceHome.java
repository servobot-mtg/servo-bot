package com.ryan_mtg.servobot.twitch.model;

import com.ryan_mtg.servobot.model.ServiceHome;

public class TwitchServiceHome implements ServiceHome {
    private long channelId;

    public TwitchServiceHome(final long channelId) {
        this.channelId = channelId;
    }

    public long getChannelId() {
        return channelId;
    }
}
