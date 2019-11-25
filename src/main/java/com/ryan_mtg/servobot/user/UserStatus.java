package com.ryan_mtg.servobot.user;

import com.ryan_mtg.servobot.twitch.model.TwitchUserStatus;

public class UserStatus {
    private TwitchUserStatus twitchStatus;

    public UserStatus() {
        twitchStatus = new TwitchUserStatus(false, false);
    }

    public UserStatus(int state) {
        twitchStatus = new TwitchUserStatus(state);
    }

    public boolean isModerator() {
        return twitchStatus.isModerator();
    }

    public boolean isSubscriber() {
        return twitchStatus.isSubscriber();
    }
}
