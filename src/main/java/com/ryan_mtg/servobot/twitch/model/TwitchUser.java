package com.ryan_mtg.servobot.twitch.model;

import com.ryan_mtg.servobot.model.User;

public class TwitchUser implements User {
    private com.ryan_mtg.servobot.user.User user;
    private TwitchUserStatus status;

    public TwitchUser(final com.ryan_mtg.servobot.user.User user, final TwitchUserStatus status) {
        this.user = user;
        this.status = status;
    }

    @Override
    public String getName() {
        return user.getTwitchUsername();
    }

    @Override
    public boolean isBot() {
        return getName().toLowerCase().endsWith("bot");
    }

    public boolean isModerator() {
        return status.isModerator();
    }

    public boolean isSubscriber() {
        return status.isSubscriber();
    }
}
