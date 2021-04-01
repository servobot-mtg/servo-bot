package com.ryan_mtg.servobot.twitch.event;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.ryan_mtg.servobot.events.MessageHomeEvent;
import com.ryan_mtg.servobot.model.BotHome;
import com.ryan_mtg.servobot.model.Message;
import com.ryan_mtg.servobot.twitch.model.TwitchMessage;
import com.ryan_mtg.servobot.twitch.model.TwitchUser;
import lombok.Getter;

public class TwitchMessageSentEvent extends TwitchHomeEvent implements MessageHomeEvent {
    private final ChannelMessageEvent event;

    @Getter
    private final TwitchUser sender;

    public TwitchMessageSentEvent(final TwitchClient client, final ChannelMessageEvent event, final BotHome botHome,
            final TwitchUser sender) {
        super(client, botHome, event.getChannel().getName(), Long.parseLong(event.getChannel().getId()));
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
