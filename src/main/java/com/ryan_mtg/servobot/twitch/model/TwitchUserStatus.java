package com.ryan_mtg.servobot.twitch.model;

public class TwitchUserStatus {
    private static final int MOD_BIT = 1;
    private static final int SUB_BIT = 2;

    private int state;

    public TwitchUserStatus(final int state) {
        this.state = state;
    }

    public TwitchUserStatus(final boolean isModerator, final boolean isSubscriber) {
        if (isModerator) {
            state += MOD_BIT;
        }

        if (isSubscriber) {
            state += SUB_BIT;
        }
    }

    public boolean isModerator() {
        return (state & MOD_BIT) != 0;
    }

    public boolean isSubscriber() {
        return (state & SUB_BIT) != 0;
    }

    public int getState() {
        return state;
    }
}
