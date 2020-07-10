package com.ryan_mtg.servobot.events;

import com.ryan_mtg.servobot.model.BotHome;
import com.ryan_mtg.servobot.model.Service;
import com.ryan_mtg.servobot.model.ServiceHome;
import lombok.Getter;

public class BotHomeAlertEvent extends AbstractHomedEvent implements AlertEvent {
    @Getter
    private String alertToken;

    @Getter
    private ServiceHome serviceHome;

    public BotHomeAlertEvent(final BotHome botHome, final String alertToken, final ServiceHome serviceHome) {
        super(botHome);
        this.alertToken = alertToken;
        this.serviceHome = serviceHome;
        setHomeEditor(serviceHome.getHomeEditor());
    }

    @Override
    public int getServiceType() {
        return Service.NO_SERVICE_TYPE;
    }
}
