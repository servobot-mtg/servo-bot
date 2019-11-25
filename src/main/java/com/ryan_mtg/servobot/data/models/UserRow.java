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

    @Column(name = "twitch_id")
    private int twitchId;

    @Column(name = "discord_id")
    private long discordId;

    @Column(name = "twitch_username")
    private String twitchUsername;

    public int getId() {
        return id;
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
}
