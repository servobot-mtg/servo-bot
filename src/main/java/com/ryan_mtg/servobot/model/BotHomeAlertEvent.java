package com.ryan_mtg.servobot.model;

import com.ryan_mtg.servobot.events.AlertEvent;

public class BotHomeAlertEvent implements AlertEvent {
    private int homeId;
    private String alertToken;
    private Home home;

    public BotHomeAlertEvent(final int homeId, final String alertToken, final Home home) {
        this.alertToken = alertToken;
        this.homeId = homeId;
        this.home = home;
    }

    @Override
    public int getHomeId() {
        return homeId;
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
