package com.ryan_mtg.servobot.data.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "home")
public class BotHomeRow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    private String homeName;

    @Column(name = "time_zone")
    private String timeZone;

    public int getId() {
        return id;
    }

    public String getHomeName() {
        return homeName;
    }

    public String getTimeZone() {
        return timeZone;
    }
}
