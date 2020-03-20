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
@Table(name = "reaction")
@Getter @Setter
public class ReactionRow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int filter;

    @Column(name = "filter_value")
    private int filterValue;

    @Column(name = "bot_home_id")
    private int botHomeId;

    @Size(max = Validation.MAX_EMOTE_LENGTH)
    private String emote;

    private boolean secure;
}
