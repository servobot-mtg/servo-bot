package com.ryan_mtg.servobot.events;

import com.ryan_mtg.servobot.model.BotHome;
import com.ryan_mtg.servobot.model.Home;
import com.ryan_mtg.servobot.model.Service;
import lombok.Getter;

public class BotHomeAlertEvent extends AbstractHomedEvent implements AlertEvent {
    @Getter
    private String alertToken;

    @Getter
    private Home home;

    public BotHomeAlertEvent(final BotHome botHome, final String alertToken, final Home home) {
        super(botHome);
        this.alertToken = alertToken;
        this.home = home;
        setHomeEditor(home.getHomeEditor());
    }

    @Override
    public int getServiceType() {
        return Service.NO_SERVICE_TYPE;
    }
}
