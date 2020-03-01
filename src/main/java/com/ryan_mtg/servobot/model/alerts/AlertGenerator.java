package com.ryan_mtg.servobot.model.alerts;

import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.utility.Validation;

import java.time.Instant;

public abstract class AlertGenerator {
    private int id;
    private String alertToken;

    protected AlertGenerator(final int id, final String alertToken) throws BotErrorException {
        this.id = id;
        this.alertToken = alertToken;

        Validation.validateStringLength(alertToken, Validation.MAX_TRIGGER_LENGTH, "Alert token");
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
