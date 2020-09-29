package com.ryan_mtg.servobot.twitch.model;

import com.ryan_mtg.servobot.model.Emote;
import com.ryan_mtg.servobot.model.Message;
import com.ryan_mtg.servobot.model.User;
import com.ryan_mtg.servobot.twitch.event.TwitchMessageSentEvent;

public class TwitchMessage implements Message {
    private TwitchMessageSentEvent messageSentEvent;

    public TwitchMessage(final TwitchMessageSentEvent messageSentEvent) {
        this.messageSentEvent = messageSentEvent;
    }

    @Override
    public long getId() {
        return 0;
    }

    @Override
    public long getChannelId() {
        return 0;
    }

    @Override
    public String getContent() {
        return messageSentEvent.getContent();
    }

    @Override
    public User getSender() {
        return messageSentEvent.getSender();
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
