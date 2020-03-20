package com.ryan_mtg.servobot.twitch.event;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.ryan_mtg.servobot.events.MessageSentEvent;
import com.ryan_mtg.servobot.model.Message;
import com.ryan_mtg.servobot.twitch.model.TwitchMessage;
import com.ryan_mtg.servobot.twitch.model.TwitchUser;
import lombok.Getter;

public class TwitchMessageSentEvent extends TwitchHomeEvent implements MessageSentEvent {
    private ChannelMessageEvent event;

    @Getter
    private TwitchUser sender;

    public TwitchMessageSentEvent(final TwitchClient client, final ChannelMessageEvent event, final int homeId,
                                  final TwitchUser sender) {
        super(client, homeId, event);
        this.event = event;
        this.sender = sender;
    }

    @Override
    public Message getMessage() {
        return new TwitchMessage(this);
    }

    public String getContent() {
        return event.getMessage();
    }
}
