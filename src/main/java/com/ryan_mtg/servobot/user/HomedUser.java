package com.ryan_mtg.servobot.user;

public class HomedUser extends User {
    private UserStatus userStatus;

    public HomedUser(final int id, final boolean admin, final int twitchId, final String twitchUsername,
                     final long discordId, final String discordUsername, final String arenaUsername,
                     final UserStatus userStatus) {
        super(id, admin, twitchId, twitchUsername, discordId, discordUsername, arenaUsername);
        this.userStatus = userStatus;
    }

    public boolean isModerator() {
        return userStatus.isModerator();
    }

    public boolean isSubscriber() {
        return userStatus.isSubscriber();
    }
}
