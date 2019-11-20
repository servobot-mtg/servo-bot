package com.ryan_mtg.servobot.model;

import com.ryan_mtg.servobot.events.AlertEvent;
import com.ryan_mtg.servobot.events.BotHomeAlertEvent;
import com.ryan_mtg.servobot.events.HomeDelegatingListener;
import com.ryan_mtg.servobot.model.alerts.AlertGenerator;
import com.ryan_mtg.servobot.model.alerts.AlertGeneratorQueue;
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

    public Bot(final String name, final Map<Integer, Service> services) {
        this.name = name;
        this.services = services;
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

    public Service getService(final int serviceType) {
        return services.get(serviceType);
    }

    public void alert(final BotHome botHome, final String alertToken) {
        AlertEvent alertEvent =
                new BotHomeAlertEvent(botHome.getId(), alertToken, new MultiServiceHome(botHome.getServiceHomes()));
        listener.onAlert(alertEvent);
    }

    private void startAlertQueue() {
        AlertGeneratorQueue queue = new AlertGeneratorQueue(this);
        for(BotHome home : homes) {
            for (AlertGenerator alertGenerator : home.getAlertGenerators()) {
                alertGenerator.setTimeZone(home.getTimeZone());
                queue.add(home, alertGenerator);
            }
        }
        queue.start();
    }
}
