package com.ryan_mtg.servobot.data.models;

import com.ryan_mtg.servobot.utility.Validation;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

@Entity
@Table(name = "user")
@Getter @Setter
public class UserRow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int flags;

    @Column(name = "twitch_id")
    private int twitchId;

    @Column(name = "discord_id")
    private long discordId;

    @Column(name = "twitch_username")
    @Size(max = Validation.MAX_USERNAME_LENGTH)
    private String twitchUsername;

    @Column(name = "discord_username")
    @Size(max = Validation.MAX_USERNAME_LENGTH)
    private String discordUsername;

    @Column(name = "arena_username")
    @Size(max = Validation.MAX_USERNAME_LENGTH)
    private String arenaUsername;
}
