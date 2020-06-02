package com.ryan_mtg.servobot.user;

import com.ryan_mtg.servobot.discord.model.DiscordUserStatus;
import com.ryan_mtg.servobot.twitch.model.TwitchUserStatus;

public class UserStatus {
    private TwitchUserStatus twitchStatus;
    private DiscordUserStatus discordUserStatus;
    private static final int TWITCH_BITS = (1 << 8) - 1;
    private static final int DISCORD_SHIFT = 8;
    private static final int DISCORD_BITS = ((1 << 8) - 1) << DISCORD_SHIFT;
    private static final int MEMBER_BIT = 1 << (8 + 8);

    public UserStatus() {
        this(MEMBER_BIT);
    }

    public UserStatus(int state) {
        twitchStatus = new TwitchUserStatus(getTwitchBits(state));
        discordUserStatus = new DiscordUserStatus(getDiscordBits(state));
    }

    public boolean isMember() {
        return true;
    }

    public boolean isModerator() {
        return twitchStatus.isModerator() || discordUserStatus.isModerator();
    }

    public boolean isSubscriber() {
        return twitchStatus.isSubscriber() || discordUserStatus.isSubscriber();
    }

    public boolean isVip() {
        return twitchStatus.isVip();
    }

    public boolean isStreamer() {
        return twitchStatus.isStreamer() || discordUserStatus.isStreamer();
    }

    public int getState() {
        return twitchStatus.getState() | discordUserStatus.getState()<<DISCORD_SHIFT | MEMBER_BIT;
    }

    public void update(final TwitchUserStatus twitchStatus) {
        this.twitchStatus = twitchStatus;
    }

    public void update(final DiscordUserStatus discordUserStatus) {
        this.discordUserStatus = discordUserStatus;
    }

    public void merge(final int state) {
        twitchStatus.merge(getTwitchBits(state));
        discordUserStatus.merge(getDiscordBits(state));
    }

    private static int getTwitchBits(final int state) {
        return state & TWITCH_BITS;
    }

    private static int getDiscordBits(final int state) {
        return (state & DISCORD_BITS) >> DISCORD_SHIFT;
    }
}
