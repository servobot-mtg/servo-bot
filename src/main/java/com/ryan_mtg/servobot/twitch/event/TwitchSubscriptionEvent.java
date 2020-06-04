package com.ryan_mtg.servobot.twitch.event;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.chat.events.channel.SubscriptionEvent;
import com.ryan_mtg.servobot.events.UserHomeEvent;
import com.ryan_mtg.servobot.model.BotHome;
import com.ryan_mtg.servobot.model.User;
import com.ryan_mtg.servobot.twitch.model.TwitchUser;

public class TwitchSubscriptionEvent extends TwitchHomeEvent implements UserHomeEvent {
    private TwitchUser subscriber;

    public TwitchSubscriptionEvent(final TwitchClient client, final SubscriptionEvent event, final BotHome botHome,
                                   final TwitchUser subscriber) {
        super(client, botHome, event.getChannel().getName());
        this.subscriber = subscriber;
    }

    @Override
    public User getUser() {
        return subscriber;
    }
}
