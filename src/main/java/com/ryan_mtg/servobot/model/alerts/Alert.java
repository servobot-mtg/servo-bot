package com.ryan_mtg.servobot.model.alerts;

import java.time.Duration;

public class Alert {
    private Duration delay;
    private String token;

    public Alert(final Duration delay, final String token) {
        this.delay = delay;
        this.token = token;
    }

    public Duration getDelay() {
        return delay;
    }

    public String getToken() {
        return token;
    }
}
