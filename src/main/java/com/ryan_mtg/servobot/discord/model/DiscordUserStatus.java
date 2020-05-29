package com.ryan_mtg.servobot.discord.model;

public class DiscordUserStatus {
    private static final int MOD_BIT = 1;
    private static final int SUB_BIT = 2;
    private static final int STREAMER_BIT = 4;

    private int state;

    public DiscordUserStatus(final int state) {
        this.state = state;
    }

    public DiscordUserStatus(final boolean isModerator, final boolean isSubscriber, final boolean isStreamer) {
        if (isModerator) {
            state |= MOD_BIT;
        }

        if (isSubscriber) {
            state |= SUB_BIT;
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

    public boolean isStreamer() {
        return (state & STREAMER_BIT) != 0;
    }

    public int getState() {
        return state;
    }

    public void merge(final int state) {
        this.state |= state;
    }
}
