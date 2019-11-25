package com.ryan_mtg.servobot.model;

import com.ryan_mtg.servobot.data.factories.SerializerContainer;
import com.ryan_mtg.servobot.events.HomeDelegatingListener;
import com.ryan_mtg.servobot.model.alerts.AlertQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Bot {
    private static Logger LOGGER = LoggerFactory.getLogger(Bot.class);
    private String name;
    private List<BotHome> homes = new ArrayList<>();
    private HomeDelegatingListener listener;
    private Map<Integer, Service> services;
    private Map<Integer, HomeEditor> homeEditorMap = new HashMap<>();
    private SerializerContainer serializers;
    private AlertQueue alertQueue = new AlertQueue(this);

    public Bot(final String name, final Map<Integer, Service> services, final SerializerContainer serializers) {
        this.name = name;
        this.services = services;
        this.serializers = serializers;
        listener = new HomeDelegatingListener(homeEditorMap);
    }

    public String getName() {
        return name;
    }

    public void addHome(final BotHome home) {
        homes.add(home);
        homeEditorMap.put(home.getId(), new HomeEditor(this, home));
        listener.register(home);
        services.values().stream().forEach(service -> service.register(home));
    }

    public void startBot() throws Exception {
        for (Service service : services.values()) {
            service.start(listener);
        }

        homes.stream().forEach(home -> home.startServices(homeEditorMap.get(home.getId())));
        startAlertQueue();
    }

    public List<BotHome> getHomes() {
        return homes;
    }

    public HomeEditor getHomeEditor(final int botHomeId) {
        return homeEditorMap.get(botHomeId);
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

    private void startAlertQueue() {
        for(BotHome home : homes) {
            alertQueue.update(home);
        }
        alertQueue.start();
    }
}
