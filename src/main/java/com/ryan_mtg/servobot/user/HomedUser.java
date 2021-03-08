package com.ryan_mtg.servobot.user;

import lombok.Getter;

public class HomedUser {
    @Getter
    private User user;
    @Getter
    private UserStatus userStatus;

    public HomedUser(final User user, final UserStatus userStatus) {
        this.user = user;
        this.userStatus = userStatus;
    }

    @Override
    public String toString() {
        return String.format("user: %s", getName());
    }

    public boolean isStreamer() {
        return userStatus.isStreamer();
    }

    public boolean isModerator() {
        return userStatus.isModerator();
    }

    public boolean isVip() {
        return userStatus.isVip();
    }

    public boolean isSubscriber() {
        return userStatus.isSubscriber();
    }

    public boolean isMember() {
        return userStatus.isMember();
    }

    public int getId() {
        return user.getId();
    }

    public String getName() {
        return user.getName();
    }

    public boolean isAdmin() {
        return user.isAdmin();
    }

    public int getTwitchId() {
        return user.getTwitchId();
    }

    public String getTwitchUsername() {
        return user.getTwitchUsername();
    }

    public long getDiscordId() {
        return user.getDiscordId();
    }

    public String getDiscordUsername() {
        return user.getDiscordUsername();
    }

    public String getArenaUsername() {
        return user.getArenaUsername();
    }
}
