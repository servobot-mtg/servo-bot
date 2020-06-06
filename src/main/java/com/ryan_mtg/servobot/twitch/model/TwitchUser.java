package com.ryan_mtg.servobot.twitch.model;

import com.github.twitch4j.chat.TwitchChat;
import com.ryan_mtg.servobot.model.User;
import com.ryan_mtg.servobot.user.HomedUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TwitchUser implements User {
    private static Logger LOGGER = LoggerFactory.getLogger(TwitchUser.class);

    private TwitchChat chat;
    private HomedUser user;

    public TwitchUser(final TwitchChat chat, final HomedUser user) {
        this.chat = chat;
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
