package com.ryan_mtg.servobot.data.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "command_alert")
public class CommandAlertRow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "command_id")
    private int commandId;

    @Column(name = "alert_token")
    private String alertToken;

    public CommandAlertRow() {}

    public CommandAlertRow(final int id, final int commandId, final String alertToken) {
        this.id = id;
        this.commandId = commandId;
        this.alertToken = alertToken;
    }

    public int getId() {
        return id;
    }

    public int getCommandId() {
        return commandId;
    }

    public void setCommandId(final int commandId) {
        this.commandId = commandId;
    }

    public String getAlertToken() {
        return alertToken;
    }

    public void setAlertToken(final String alertToken) {
        this.alertToken = alertToken;
    }
}
