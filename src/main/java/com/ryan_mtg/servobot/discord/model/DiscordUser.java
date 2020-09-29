package com.ryan_mtg.servobot.discord.model;

import com.ryan_mtg.servobot.model.User;
import com.ryan_mtg.servobot.user.HomedUser;
import lombok.Getter;

public class DiscordUser implements User {
    @Getter
    private com.ryan_mtg.servobot.user.User user;

    @Getter
    private net.dv8tion.jda.api.entities.User discordUser;

    public DiscordUser(final com.ryan_mtg.servobot.user.User user,
                            final net.dv8tion.jda.api.entities.User discordUser) {
        this.user = user;
        this.discordUser = discordUser;
    }

    @Override
    public String getName() {
        return discordUser.getName();
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
    public boolean isBot() {
        return discordUser.isBot();
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

    public long getDiscordId() {
        return discordUser.getIdLong();
    }
}
