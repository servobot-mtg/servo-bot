package com.ryan_mtg.servobot.twitch.event;

import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.ryan_mtg.servobot.events.MessageSentEvent;
import com.ryan_mtg.servobot.model.HomeEditor;
import com.ryan_mtg.servobot.model.Message;
import com.ryan_mtg.servobot.model.User;
import com.ryan_mtg.servobot.twitch.model.TwitchChannel;
import com.ryan_mtg.servobot.twitch.model.TwitchMessage;
import com.ryan_mtg.servobot.twitch.model.TwitchUser;

public class TwitchMessageSentEvent implements MessageSentEvent {
    private int homeId;
    private ChannelMessageEvent event;
    private TwitchUser sender;
    private TwitchChannel twitchChannel;
    private HomeEditor homeEditor;

    public TwitchMessageSentEvent(final ChannelMessageEvent event, int homeId, final TwitchUser sender) {
        this.homeId = homeId;
        this.event = event;
        this.sender = sender;
    }

    @Override
    public Message getMessage() {
        return new TwitchMessage(this);
    }

    @Override
    public User getSender() {
        return sender;
    }

    @Override
    public int getHomeId() {
        return homeId;
    }

    @Override
    public HomeEditor getHomeEditor() {
        return homeEditor;
    }

    @Override
    public void setHomeEditor(final HomeEditor homeEditor) {
        this.homeEditor = homeEditor;
    }

    public TwitchChannel getChannel() {
        if (twitchChannel == null) {
            twitchChannel = new TwitchChannel(event.getTwitchChat(), event.getChannel().getName(), getHomeEditor());
        }
        return twitchChannel;
    }

    public String getContent() {
        return event.getMessage();
    }
}
