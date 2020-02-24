package com.ryan_mtg.servobot.user;

import com.ryan_mtg.servobot.data.models.UserRow;
import com.ryan_mtg.servobot.events.BotErrorException;

public class User {
    public static final int UNREGISTERED_ID = 0;
    public static final int INVITE_FLAG = 1<<1;

    private static final int MAX_USERNAME_SIZE = UserRow.MAX_USERNAME_SIZE;
    private static final int ADMIN_FLAG = 1;

    private int id;
    private int flags;
    private int twitchId;
    private long discordId;
    private String twitchUsername;
    private String discordUsername;
    private String arenaUsername;

    public User(final int id, final int flags, final int twitchId, final String twitchUsername,
                final long discordId, final String discordUsername, final String arenaUsername)
            throws BotErrorException {
        this.id = id;
        this.flags = flags;
        this.twitchId = twitchId;
        this.twitchUsername = twitchUsername;
        this.discordId = discordId;
        this.discordUsername = discordUsername;
        this.arenaUsername = arenaUsername;

        if (twitchUsername != null && twitchUsername.length() > MAX_USERNAME_SIZE) {
            throw new BotErrorException(
                    String.format("Twitch username too long (max %d): %s", MAX_USERNAME_SIZE, twitchUsername));
        }

        if (discordUsername != null && discordUsername.length() > MAX_USERNAME_SIZE) {
            throw new BotErrorException(
                    String.format("Discord username too long (max %d): %s", MAX_USERNAME_SIZE, discordUsername));
        }

        if (arenaUsername != null && arenaUsername.length() > MAX_USERNAME_SIZE) {
            throw new BotErrorException(
                    String.format("Arena username too long (max %d): %s", MAX_USERNAME_SIZE, arenaUsername));
        }
    }

    public int getId() {
        return id;
    }

    public int getFlags() {
        return flags;
    }

    public boolean isAdmin() {
        return isFlagSet(ADMIN_FLAG);
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

    public boolean hasInvite() {
        return isFlagSet(INVITE_FLAG);
    }

    public void removeInvite() {
        setFlag(INVITE_FLAG, false);
    }

    private boolean isFlagSet(final int flag) {
        return (flags & flag) != 0;
    }

    private void setFlag(final int flag, final boolean value) {
        if (value) {
            flags = flags | flag;
        } else {
            flags = flags & ~flag;
        }
    }
}
