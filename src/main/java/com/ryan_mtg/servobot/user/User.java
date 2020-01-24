package com.ryan_mtg.servobot.user;

public class User {
    public static final int UNREGISTERED_ID = 0;

    private int id;
    private boolean admin;
    private int twitchId;
    private long discordId;
    private String twitchUsername;
    private String discordUsername;
    private String arenaUsername;

    public User(final int id, final boolean admin, final int twitchId, final String twitchUsername,
                final long discordId, final String discordUsername, final String arenaUsername) {
        this.id = id;
        this.admin = admin;
        this.twitchId = twitchId;
        this.twitchUsername = twitchUsername;
        this.discordId = discordId;
        this.discordUsername = discordUsername;
        this.arenaUsername = arenaUsername;
    }

    public int getId() {
        return id;
    }

    public boolean isAdmin() {
        return admin;
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

    public String getDiscordUsername() {
        return discordUsername;
    }

    public String getArenaUsername() {
        return arenaUsername;
    }

    public String getName() {
        if (twitchUsername != null) {
            return twitchUsername;
        }
        return discordUsername;
    }
}
