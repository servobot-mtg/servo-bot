package com.ryan_mtg.servobot.twitch.event;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.chat.events.channel.SubscriptionEvent;
import com.ryan_mtg.servobot.events.UserEvent;
import com.ryan_mtg.servobot.model.User;
import com.ryan_mtg.servobot.twitch.model.TwitchUser;

public class TwitchSubscriptionEvent extends TwitchHomeEvent implements UserEvent {
    private TwitchUser subscriber;

    public TwitchSubscriptionEvent(final TwitchClient client, final SubscriptionEvent event, final int homeId,
                                   final TwitchUser subscriber) {
        super(client, homeId, event);
        this.subscriber = subscriber;
    }

    @Override
    public User getUser() {
        return subscriber;
    }
}
