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
@Table(name = "alert_generator")
public class AlertGeneratorRow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter @Setter
    private int id;

    @Column(name = "bot_home_id")
    @Getter @Setter
    private int botHomeId;

    @Getter @Setter
    private int type;

    @Getter @Setter
    private int time;

    @Column(name = "alert_token")
    @Size(max = Validation.MAX_TRIGGER_LENGTH)
    @Getter @Setter
    private String alertToken;
}

