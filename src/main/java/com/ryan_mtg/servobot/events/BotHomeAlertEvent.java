package com.ryan_mtg.servobot.events;

import com.ryan_mtg.servobot.model.Home;
import com.ryan_mtg.servobot.model.Service;

public class BotHomeAlertEvent extends AbstractHomedEvent implements AlertEvent {
    private String alertToken;
    private Home home;

    public BotHomeAlertEvent(final int homeId, final String alertToken, final Home home) {
        super(homeId);
        setHomeEditor(home.getHomeEditor());
        this.alertToken = alertToken;
        this.home = home;
        setHomeEditor(home.getHomeEditor());
    }

    @Override
    public String getAlertToken() {
        return alertToken;
    }

    @Override
    public Home getHome() {
        return home;
    }

    @Override
    public int getServiceType() {
        return Service.NO_SERVICE_TYPE;
    }
}
