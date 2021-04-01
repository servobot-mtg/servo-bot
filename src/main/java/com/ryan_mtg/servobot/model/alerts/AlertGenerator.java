package com.ryan_mtg.servobot.model.alerts;

import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.utility.Validation;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

public abstract class AlertGenerator {
    public static final int UNREGISTERED_ID = 0;

    @Getter @Setter
    private int id;

    @Getter
    private final String alertToken;

    AlertGenerator(final int id, final String alertToken) throws UserError {
        this.id = id;
        this.alertToken = alertToken;

        Validation.validateStringValue(alertToken, Validation.MAX_TRIGGER_LENGTH, "Alert Keyword",
                Validation.NAME_PATTERN);
    }

    public abstract int getType();
    public abstract String getDescription();
    public abstract void setTimeZone(String timeZone);
    public abstract Instant getNextAlertTime(Instant now);
}
