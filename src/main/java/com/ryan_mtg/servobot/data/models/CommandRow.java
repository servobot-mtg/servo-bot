package com.ryan_mtg.servobot.data.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "command")
public class CommandRow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int type;

    @Column(name = "bot_home_id")
    private int botHomeId;

    private String stringParameter;
    private String stringParameter2;

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public int getBotHomeId() {
        return botHomeId;
    }

    public void setBotHomeId(final int botHomeId) {
        this.botHomeId = botHomeId;
    }

    public void setType(final int type) {
        this.type = type;
    }

    public String getStringParameter() {
        return stringParameter;
    }

    public void setStringParameter(final String stringParameter) {
        this.stringParameter = stringParameter;
    }

    public String getStringParameter2() {
        return stringParameter2;
    }

    public void setStringParameter2(final String stringParameter2) {
        this.stringParameter2 = stringParameter2;
    }
}
