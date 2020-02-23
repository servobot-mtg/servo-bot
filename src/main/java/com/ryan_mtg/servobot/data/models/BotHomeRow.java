package com.ryan_mtg.servobot.data.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

@Entity
@Table(name = "home")
public class BotHomeRow {
    public static final int MAX_NAME_SIZE = 30;
    public static final int MAX_TIME_ZONE_SIZE = 60;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    @Size(max = MAX_NAME_SIZE)
    private String homeName;

    @Column(name = "bot_name")
    @Size(max = MAX_NAME_SIZE)
    private String botName;

    @Column(name = "time_zone")
    private String timeZone;

    public int getId() {
        return id;
    }

    public String getHomeName() {
        return homeName;
    }

    public String getBotName() {
        return botName;
    }

    public void setBotName(final String botName) {
        this.botName = botName;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(final String timeZone) {
        this.timeZone = timeZone;
    }
}
