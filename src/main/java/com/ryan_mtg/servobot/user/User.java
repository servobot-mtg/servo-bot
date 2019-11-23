package com.ryan_mtg.servobot.user;

public class User {
    public static final int UNREGISTERED_ID = 0;

    private int id;

    private int twitchId;

    private long discordId;

    private String twitchUsername;

    public User(final int id, final int twitchId, final String twitchUsername, final long discordId) {
        this.id = id;
        this.twitchId = twitchId;
        this.twitchUsername = twitchUsername;
        this.discordId = discordId;
    }

    public int getId() {
        return id;
    }

    public int getTwitchId() {
        return twitchId;
    }

    public long getDiscordId() {
        return discordId;
    }

    public String getTwitchUsername() {
        return twitchUsername;
    }
}
