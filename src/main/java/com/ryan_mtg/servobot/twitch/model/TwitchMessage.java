package com.ryan_mtg.servobot.twitch.model;

import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.ryan_mtg.servobot.model.Channel;
import com.ryan_mtg.servobot.model.Emote;
import com.ryan_mtg.servobot.model.Home;
import com.ryan_mtg.servobot.model.Message;
import com.ryan_mtg.servobot.model.User;

public class TwitchMessage implements Message {
    private ChannelMessageEvent messageEvent;

    public TwitchMessage(final ChannelMessageEvent messageEvent) {
        this.messageEvent = messageEvent;
    }

    @Override
    public String getContent() {
        return messageEvent.getMessage();
    }

    @Override
    public Channel getChannel() {
        return new TwitchChannel(messageEvent.getTwitchChat(), messageEvent.getChannel());
    }

    @Override
    public Home getHome() {
        return new TwitchChannel(messageEvent.getTwitchChat(), messageEvent.getChannel());
    }

    @Override
    public User getSender() {
        return new TwitchUser(messageEvent.getUser());
    }

    @Override
    public boolean canEmote() {
        return false;
    }

    @Override
    public void addEmote(final Emote emote) {
        throw new IllegalStateException("Twitch doesn't have reactions");
    }
}
