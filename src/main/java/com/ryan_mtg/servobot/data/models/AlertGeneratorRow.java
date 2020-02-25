package com.ryan_mtg.servobot.data.models;

import com.ryan_mtg.servobot.utility.Validation;

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
    private int id;

    @Column(name = "bot_home_id")
    private int botHomeId;

    private int type;
    private int time;

    @Column(name = "alert_token")
    @Size(max = Validation.MAX_TRIGGER_LENGTH)
    private String alertToken;

    public int getId() {
        return id;
    }

    public int getBotHomeId() {
        return botHomeId;
    }

    public int getType() {
        return type;
    }

    public int getTime() {
        return time;
    }

    public String getAlertToken() {
        return alertToken;
    }
}

