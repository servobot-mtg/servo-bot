package com.ryan_mtg.servobot.model.alerts;

import com.ryan_mtg.servobot.data.models.AlertGeneratorRow;
import com.ryan_mtg.servobot.events.BotErrorException;

import java.time.Instant;

public abstract class AlertGenerator {
    private static final int MAX_TOKEN_SIZE = AlertGeneratorRow.MAX_TOKEN_SIZE;

    private int id;
    private String alertToken;

    protected AlertGenerator(final int id, final String alertToken) throws BotErrorException {
        this.id = id;
        this.alertToken = alertToken;
        if (alertToken.length() > MAX_TOKEN_SIZE) {
            throw new BotErrorException(String.format("Alert token too long (max %d): %s", MAX_TOKEN_SIZE, alertToken));
        }
    }

    public abstract int getType();
    public abstract String getDescription();
    public abstract void setTimeZone(String timeZone);
    public abstract Instant getNextAlertTime(Instant now);

    public int getId() {
        return id;
    }

    public String getAlertToken() {
        return alertToken;
    }
}
