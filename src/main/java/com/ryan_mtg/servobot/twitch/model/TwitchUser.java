package com.ryan_mtg.servobot.twitch.model;

import com.ryan_mtg.servobot.model.User;
import com.ryan_mtg.servobot.user.HomedUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TwitchUser implements User {
    private static final Logger LOGGER = LoggerFactory.getLogger(TwitchUser.class);

    private final HomedUser user;

    public TwitchUser(final HomedUser user) {
        this.user = user;
    }

    @Override
    public String getName() {
        return user.getTwitchUsername();
    }

    @Override
    public int getId() {
        return user.getId();
    }

    @Override
    public HomedUser getHomedUser() {
        return user;
    }

    @Override
    public com.ryan_mtg.servobot.user.User getUser() {
        return user.getUser();
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
