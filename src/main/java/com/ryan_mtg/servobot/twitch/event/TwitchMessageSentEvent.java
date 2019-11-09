package com.ryan_mtg.servobot.twitch.event;

import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.ryan_mtg.servobot.events.MessageSentEvent;
import com.ryan_mtg.servobot.model.Message;
import com.ryan_mtg.servobot.model.User;
import com.ryan_mtg.servobot.twitch.model.TwitchMessage;
import com.ryan_mtg.servobot.twitch.model.TwitchUser;

public class TwitchMessageSentEvent implements MessageSentEvent {
    private int homeId;
    private ChannelMessageEvent event;

    public TwitchMessageSentEvent(final ChannelMessageEvent event, int homeId) {
        this.homeId = homeId;
        this.event = event;
    }

    @Override
    public Message getMessage() {
        return new TwitchMessage(event);
    }

    @Override
    public User getSender() {
        return new TwitchUser(event.getUser());
    }

    @Override
    public int getHomeId() {
        return homeId;
    }
}
