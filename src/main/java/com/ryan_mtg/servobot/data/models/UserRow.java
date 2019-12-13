package com.ryan_mtg.servobot.data.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "user")
public class UserRow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private boolean admin;

    @Column(name = "twitch_id")
    private int twitchId;

    @Column(name = "discord_id")
    private long discordId;

    @Column(name = "twitch_username")
    private String twitchUsername;

    @Column(name = "discord_username")
    private String discordUsername;

    @Column(name = "arena_username")
    private String arenaUsername;

    public int getId() {
        return id;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(final boolean admin) {
        this.admin = admin;
    }

    public int getTwitchId() {
        return twitchId;
    }

    public void setTwitchId(final int twitchId) {
        this.twitchId = twitchId;
    }

    public long getDiscordId() {
        return discordId;
    }

    public void setDiscordId(final long discordId) {
        this.discordId = discordId;
    }

    public String getTwitchUsername() {
        return twitchUsername;
    }

    public void setTwitchUsername(final String twitchUsername) {
        this.twitchUsername = twitchUsername;
    }

    public String getDiscordUsername() {
        return discordUsername;
    }

    public void setDiscordUsername(final String discordUsername) {
        this.discordUsername = discordUsername;
    }

    public String getArenaUsername() {
        return arenaUsername;
    }

    public void setArenaUsername(final String arenaUsername) {
        this.arenaUsername = arenaUsername;
    }
}
