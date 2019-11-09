package com.ryan_mtg.servobot.twitch.model;

import com.github.twitch4j.common.events.domain.EventUser;
import com.ryan_mtg.servobot.model.User;

public class TwitchUser implements User {
    private EventUser user;

    public TwitchUser(EventUser user) {
        this.user = user;
    }

    @Override
    public String getName() {
        return user.getName();
    }

    @Override
    public boolean isBot() {
        return getName().toLowerCase().endsWith("bot");
    }
}
