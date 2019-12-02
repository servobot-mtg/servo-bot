package com.ryan_mtg.servobot.twitch.model;

import com.ryan_mtg.servobot.model.User;
import com.ryan_mtg.servobot.user.HomedUser;

public class TwitchUser implements User {
    private HomedUser user;

    public TwitchUser(final HomedUser user) {
        this.user = user;
    }

    @Override
    public String getName() {
        return user.getTwitchUsername();
    }

    @Override
    public boolean isBot() {
        return getName().toLowerCase().endsWith("bot");
    }

    @Override
    public boolean isAdmin() {
        return user.isAdmin();
    }

    @Override
    public boolean isModerator() {
        return user.isModerator();
    }

    @Override
    public boolean isSubscriber() {
        return user.isSubscriber();
    }
}
