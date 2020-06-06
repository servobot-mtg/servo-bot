package com.ryan_mtg.servobot.discord.event;

import com.ryan_mtg.servobot.model.User;
import com.ryan_mtg.servobot.user.HomedUser;

public class DiscordGlobalUser implements User {
    private final net.dv8tion.jda.api.entities.User discordUser;
    private final com.ryan_mtg.servobot.user.User user;

    public DiscordGlobalUser(final net.dv8tion.jda.api.entities.User discordUser,
            final com.ryan_mtg.servobot.user.User user) {
        this.discordUser = discordUser;
        this.user = user;
    }

    @Override
    public String getName() {
        return user.getName();
    }

    @Override
    public int getId() {
        return user.getId();
    }

    @Override
    public HomedUser getHomedUser() {
        return null;
    }

    @Override
    public com.ryan_mtg.servobot.user.User getUser() {
        return user;
    }

    @Override
    public boolean isBot() {
        return false;
    }

    @Override
    public boolean isAdmin() {
        return user.isAdmin();
    }

    @Override
    public boolean isModerator() {
        return false;
    }

    @Override
    public boolean isSubscriber() {
        return false;
    }
}
