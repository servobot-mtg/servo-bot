package com.ryan_mtg.servobot.events;

import com.ryan_mtg.servobot.model.Home;

public class BotHomeAlertEvent extends AbstractHomedEvent implements AlertEvent {
    private String alertToken;
    private Home home;

    public BotHomeAlertEvent(final int homeId, final String alertToken, final Home home) {
        super(homeId);
        this.alertToken = alertToken;
        this.home = home;
    }

    @Override
    public String getAlertToken() {
        return alertToken;
    }

    @Override
    public Home getHome() {
        return home;
    }
}
