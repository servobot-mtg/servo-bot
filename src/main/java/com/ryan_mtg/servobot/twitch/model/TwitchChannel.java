package com.ryan_mtg.servobot.twitch.model;

import com.github.twitch4j.chat.TwitchChat;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.model.Channel;
import com.ryan_mtg.servobot.model.Emote;
import com.ryan_mtg.servobot.model.Home;
import com.ryan_mtg.servobot.model.HomeEditor;
import com.ryan_mtg.servobot.model.User;

public class TwitchChannel implements Channel, Home {
    private TwitchChat twitchChat;
    private String channelName;
    private HomeEditor homeEditor;

    public TwitchChannel(final TwitchChat twitchChat, final String channelName, final HomeEditor homeEditor) {
        this.twitchChat = twitchChat;
        this.channelName = channelName;
        this.homeEditor = homeEditor;
    }

    @Override
    public Home getHome() {
        return this;
    }

    @Override
    public void say(final String message) {
        twitchChat.sendMessage(channelName, message);
    }

    @Override
    public String getName() {
        return channelName;
    }

    @Override
    public Channel getChannel(final String channelName, final int serviceType) {
        if (serviceType != TwitchService.TYPE) {
            return null;
        }
        if (channelName.equals(getName())) {
            return this;
        }
        throw new IllegalArgumentException(channelName + " is not a channel for " + getName());
    }

    @Override
    public boolean isStreamer(final User user) {
        return user.getName().toLowerCase().equals(getName());
    }

    @Override
    public String getRole(final User user, final int serviceType) {
        return "Unable to determine";
    }

    @Override
    public void setRole(final User user, final String role) throws BotErrorException {
        throw new BotErrorException("Twitch doesn't have roles");
    }

    @Override
    public void setRole(final String username, final String role) throws BotErrorException {
        throw new BotErrorException("Twitch doesn't have roles");
    }

    @Override
    public int clearRole(final String role) throws BotErrorException {
        throw new BotErrorException("Twitch doesn't have roles");
    }

    @Override
    public Emote getEmote(final String emoteName) {
        return null;
    }

    @Override
    public HomeEditor getHomeEditor() {
        return homeEditor;
    }

    @Override
    public void setStatus(final String status) {}
}
