package com.ryan_mtg.servobot.user;

import com.ryan_mtg.servobot.discord.model.DiscordUserStatus;
import com.ryan_mtg.servobot.twitch.model.TwitchUserStatus;

public class UserStatus {
    private TwitchUserStatus twitchStatus;
    private DiscordUserStatus discordUserStatus;
    private static int TWITCH_BITS = (1<<8)-1;
    private static int DISCORD_SHIFT = 8;
    private static int DISCORD_BITS = ((1<<8)-1)<<DISCORD_SHIFT;
    private static int MEMBER_BIT = 1<<(8+8);

    public UserStatus() {
        this(MEMBER_BIT);
    }

    public UserStatus(int state) {
        twitchStatus = new TwitchUserStatus(state & TWITCH_BITS);
        discordUserStatus = new DiscordUserStatus(state & DISCORD_BITS);
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

    public int getState() {
        return twitchStatus.getState() | discordUserStatus.getState()<<DISCORD_SHIFT | MEMBER_BIT;
    }

    public void merge(final TwitchUserStatus twitchStatus) {
        this.twitchStatus = twitchStatus;
    }

    public void merge(final DiscordUserStatus discordUserStatus) {
        this.discordUserStatus = discordUserStatus;
    }
}
