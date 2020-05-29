package com.ryan_mtg.servobot.twitch.model;

import lombok.Getter;

public class TwitchUserStatus {
    private static final int MOD_BIT = 1;
    private static final int SUB_BIT = 2;
    private static final int VIP_BIT = 4;
    private static final int STREAMER_BIT = 8;

    @Getter
    private int state;

    public TwitchUserStatus(final int state) {
        this.state = state;
    }

    public TwitchUserStatus(final boolean isModerator, final boolean isSubscriber, final boolean isVip,
                            final boolean isStreamer) {
        if (isModerator) {
            state |= MOD_BIT;
        }

        if (isSubscriber) {
            state |= SUB_BIT;
        }

        if (isVip) {
            state |= VIP_BIT;
        }

        if (isStreamer) {
            state |= STREAMER_BIT;
        }
    }

    public boolean isModerator() {
        return (state & MOD_BIT) != 0;
    }

    public boolean isSubscriber() {
        return (state & SUB_BIT) != 0;
    }

    public boolean isVip() {
        return (state & VIP_BIT) != 0;
    }

    public boolean isStreamer() {
        return (state & STREAMER_BIT) != 0;
    }

    public void merge(final int state) {
        this.state |= state;
    }
}
