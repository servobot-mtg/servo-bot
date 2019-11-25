package com.ryan_mtg.servobot.twitch.model;

import com.ryan_mtg.servobot.model.BotHome;
import com.ryan_mtg.servobot.model.Home;
import com.ryan_mtg.servobot.model.HomeEditor;
import com.ryan_mtg.servobot.model.Service;
import com.ryan_mtg.servobot.model.ServiceHome;

public class TwitchServiceHome implements ServiceHome {
    private TwitchService twitchService;
    private long channelId;
    private HomeEditor homeEditor;

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
        return twitchService.getHome(channelId, homeEditor);
    }

    @Override
    public void start(final BotHome botHome) {
        twitchService.joinChannel(channelId);
    }

    @Override
    public void setHomeEditor(final HomeEditor homeEditor) {
        this.homeEditor = homeEditor;
    }

    @Override
    public int getServiceType() {
        return TwitchService.TYPE;
    }

    @Override
    public String getDescription() {
        return String.format("Channel %s ", twitchService.getChannelName(channelId));
    }

    public long getChannelId() {
        return channelId;
    }
}
