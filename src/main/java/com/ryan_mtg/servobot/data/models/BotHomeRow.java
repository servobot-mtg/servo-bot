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
@Table(name = "home")
public class BotHomeRow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    @Size(max = Validation.MAX_NAME_LENGTH)
    private String homeName;

    @Column(name = "bot_name")
    @Size(max = Validation.MAX_NAME_LENGTH)
    private String botName;

    @Column(name = "time_zone")
    @Size(max = Validation.MAX_TIME_ZONE_LENGTH)
    private String timeZone;

    public int getId() {
        return id;
    }

    public String getHomeName() {
        return homeName;
    }

    public void setHomeName(final String homeName) {
        this.homeName = homeName;
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
