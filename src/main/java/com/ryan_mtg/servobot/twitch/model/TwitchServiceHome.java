package com.ryan_mtg.servobot.twitch.model;

import com.google.common.collect.Lists;
import com.ryan_mtg.servobot.model.BotHome;
import com.ryan_mtg.servobot.model.Home;
import com.ryan_mtg.servobot.model.HomeEditor;
import com.ryan_mtg.servobot.model.Service;
import com.ryan_mtg.servobot.model.ServiceHome;

import java.util.List;

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
    public int getServiceType() {
        return TwitchService.TYPE;
    }

    @Override
    public String getDescription() {
        return String.format("Channel %s ", getChannelName());
    }

    @Override
    public List<String> getEmotes() {
        return Lists.newArrayList();
    }

    @Override
    public List<String> getRoles() {
        return Lists.newArrayList();
    }

    @Override
    public List<String> getChannels() {
        return Lists.newArrayList(getChannelName());
    }

    @Override
    public boolean isStreaming() {
        return twitchService.isStreaming(channelId);
    }

    @Override
    public void start(final BotHome botHome) {
        twitchService.joinChannel(channelId);
    }

    @Override
    public void stop(final BotHome botHome) {
        twitchService.leaveChannel(channelId);
    }

    @Override
    public void setHomeEditor(final HomeEditor homeEditor) {
        this.homeEditor = homeEditor;
    }

    @Override
    public void setName(final String botName) {}

    @Override
    public String getLink() {
        return String.format("http://twitch.tv/%s", getChannelName());
    }

    @Override
    public String getImageUrl() {
        return twitchService.getChannelImageUrl(channelId);
    }

    public String getChannelName() {
        return twitchService.getChannelName(channelId);
    }

    public long getChannelId() {
        return channelId;
    }
}
