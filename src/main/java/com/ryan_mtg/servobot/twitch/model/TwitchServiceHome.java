package com.ryan_mtg.servobot.twitch.model;

import com.ryan_mtg.servobot.model.Home;
import com.ryan_mtg.servobot.model.Service;
import com.ryan_mtg.servobot.model.ServiceHome;

public class TwitchServiceHome implements ServiceHome {
    private TwitchService twitchService;
    private long channelId;

    public TwitchServiceHome(final TwitchService twitchService, final long channelId) {
        this.channelId = channelId;
        this.twitchService = twitchService;
    }

    @Override
    public Service getService() {
        return twitchService;
    }

    @Override
    public Home getHome() {
        return twitchService.getHome(channelId);
    }

    @Override
    public int getServiceType() {
        return TwitchService.TYPE;
    }

    @Override
    public String getDescription() {
        return String.format("Channel %s ", getHome().getName());
    }

    public long getChannelId() {
        return channelId;
    }
}
