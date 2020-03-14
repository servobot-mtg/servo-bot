package com.ryan_mtg.servobot.twitch.event;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.ryan_mtg.servobot.events.MessageSentEvent;
import com.ryan_mtg.servobot.model.BotEditor;
import com.ryan_mtg.servobot.model.Home;
import com.ryan_mtg.servobot.model.HomeEditor;
import com.ryan_mtg.servobot.model.Message;
import com.ryan_mtg.servobot.model.User;
import com.ryan_mtg.servobot.twitch.model.TwitchChannel;
import com.ryan_mtg.servobot.twitch.model.TwitchMessage;
import com.ryan_mtg.servobot.twitch.model.TwitchService;
import com.ryan_mtg.servobot.twitch.model.TwitchUser;

public class TwitchMessageSentEvent implements MessageSentEvent {
    private TwitchClient client;
    private int homeId;
    private ChannelMessageEvent event;
    private TwitchUser sender;
    private TwitchChannel twitchChannel;
    private BotEditor botEditor;
    private HomeEditor homeEditor;

    public TwitchMessageSentEvent(final TwitchClient client, final ChannelMessageEvent event, int homeId,
                                  final TwitchUser sender) {
        this.client = client;
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
    public int getServiceType() {
        return TwitchService.TYPE;
    }

    @Override
    public int getHomeId() {
        return homeId;
    }

    @Override
    public BotEditor getBotEditor() {
        return botEditor;
    }

    @Override
    public void setBotEditor(final BotEditor botEditor) {
        this.botEditor = botEditor;
    }

    @Override
    public HomeEditor getHomeEditor() {
        return homeEditor;
    }

    @Override
    public void setHomeEditor(final HomeEditor homeEditor) {
        this.homeEditor = homeEditor;
    }

    @Override
    public Home getHome() {
        return getChannel();
    }

    public TwitchChannel getChannel() {
        if (twitchChannel == null) {
            twitchChannel = new TwitchChannel(client, event.getChannel().getName(), getHomeEditor());
        }
        return twitchChannel;
    }

    public String getContent() {
        return event.getMessage();
    }
}
