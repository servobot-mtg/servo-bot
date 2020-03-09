package com.ryan_mtg.servobot.data.models;

import com.ryan_mtg.servobot.model.giveaway.Prize;
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
@Table(name = "prize")
public class PrizeRow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter @Setter
    private int id;

    @Column(name = "giveaway_id")
    @Setter
    private int giveawayId;

    @Getter @Setter
    private Prize.Status status;

    @Size(max = Validation.MAX_TEXT_LENGTH)
    @Getter @Setter
    private String reward;

    @Size(max = Validation.MAX_TEXT_LENGTH)
    @Getter @Setter
    private String description;

    @Column(name = "winner_id")
    @Getter @Setter
    private int winnerId;
}

