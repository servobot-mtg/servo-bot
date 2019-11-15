package com.ryan_mtg.servobot.data.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bot")
public class BotRow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;

    @Column(name = "discord_token")
    private String discordToken;

    @Column(name = "twitch_token")
    private String twitchToken;

    @Column(name = "twitch_client_id")
    private String twitchClientId;

    @Column(name = "twitch_secret")
    private String twitchSecret;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDiscordToken() {
        return discordToken;
    }

    public String getTwitchToken() {
        return twitchToken;
    }

    public String getTwitchClientId() {
        return twitchClientId;
    }

    public String getTwitchSecret() {
        return twitchSecret;
    }
}
