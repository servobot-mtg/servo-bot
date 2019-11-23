package com.ryan_mtg.servobot.model;

import com.ryan_mtg.servobot.data.factories.SerializerContainer;
import com.ryan_mtg.servobot.events.AlertEvent;
import com.ryan_mtg.servobot.events.BotHomeAlertEvent;
import com.ryan_mtg.servobot.events.HomeDelegatingListener;
import com.ryan_mtg.servobot.model.alerts.AlertQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Bot {
    private static Logger LOGGER = LoggerFactory.getLogger(Bot.class);
    private String name;
    private List<BotHome> homes = new ArrayList<>();
    private HomeDelegatingListener listener = new HomeDelegatingListener();
    private Map<Integer, Service> services;
    private SerializerContainer serializers;
    private AlertQueue alertQueue = new AlertQueue(this);

    public Bot(final String name, final Map<Integer, Service> services, final SerializerContainer serializers) {
        this.name = name;
        this.services = services;
        this.serializers = serializers;
    }

    public String getName() {
        return name;
    }

    public void addHome(final BotHome home) {
        homes.add(home);
        listener.register(home);
        services.values().stream().forEach(service -> service.register(home));
    }

    public void startBot() throws Exception {
        for (Service service : services.values()) {
            service.start(listener);
        }

        homes.stream().forEach(home ->
                home.getServiceHomes().values().stream().forEach(serviceHome -> serviceHome.start(home)));

        startAlertQueue();
    }

    public List<BotHome> getHomes() {
        return homes;
    }

    public BotHome getHome(final int botHomeId) {
        for(BotHome home : homes) {
            if (home.getId() == botHomeId) {
                return home;
            }
        }
        return null;
    }

    public SerializerContainer getSerializers() {
        return serializers;
    }

    public AlertQueue getAlertQueue() {
        return alertQueue;
    }

    public void alert(final BotHome botHome, final String alertToken) {
        AlertEvent alertEvent =
                new BotHomeAlertEvent(botHome.getId(), alertToken, new MultiServiceHome(botHome.getServiceHomes()));
        listener.onAlert(alertEvent);
    }

    private void startAlertQueue() {
        for(BotHome home : homes) {
            alertQueue.update(home);
        }
        alertQueue.start();
    }
}
