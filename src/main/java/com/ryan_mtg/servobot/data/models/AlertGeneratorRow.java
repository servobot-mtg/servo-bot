package com.ryan_mtg.servobot.data.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

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

