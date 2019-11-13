package com.ryan_mtg.servobot.model;

import java.time.Instant;

public abstract class AlertGenerator {
    private int id;
    private String alertToken;

    protected AlertGenerator(final int id, final String alertToken) {
        this.id = id;
        this.alertToken = alertToken;
    }

    public abstract int getType();
    public abstract Instant getNextAlertTime(Instant now);

    public String getAlertToken() {
        return alertToken;
    }
}
