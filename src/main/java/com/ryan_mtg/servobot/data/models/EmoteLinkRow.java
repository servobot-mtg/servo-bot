package com.ryan_mtg.servobot.data.models;

import com.ryan_mtg.servobot.utility.Validation;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

@Entity
@Table(name = "emote_link")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class EmoteLinkRow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private int id;

    @Column(name = "bot_home_id")
    private int botHomeId;

    @Column(name = "twitch_emote_name")
    @Size(max = Validation.MAX_EMOTE_LENGTH)
    private String twitchEmoteName;

    @Column(name = "discord_emote_name")
    @Size(max = Validation.MAX_EMOTE_LENGTH)
    private String discordEmoteName;
}
