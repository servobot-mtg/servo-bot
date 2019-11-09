package com.ryan_mtg.servobot.twitch.model;

import com.github.twitch4j.chat.TwitchChat;
import com.github.twitch4j.common.events.domain.EventChannel;
import com.ryan_mtg.servobot.model.Channel;
import com.ryan_mtg.servobot.model.Emote;
import com.ryan_mtg.servobot.model.Home;
import com.ryan_mtg.servobot.model.User;

public class TwitchChannel implements Channel, Home {
    private TwitchChat twitchChat;
    private EventChannel eventChannel;

    public TwitchChannel(final TwitchChat twitchChat, final EventChannel eventChannel) {
        this.twitchChat = twitchChat;
        this.eventChannel = eventChannel;
    }

    @Override
    public Home getHome() {
        return this;
    }

    @Override
    public void say(final String message) {
        twitchChat.sendMessage(eventChannel.getName(), message);
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Channel getChannel(final String channelName) {
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
    public String getRole(final User user) {
        return "Unable to determine";
    }

    @Override
    public boolean hasEmotes() {
        return false;
    }

    @Override
    public Emote getEmote(final String emoteName) {
        return null;
    }
}
