package com.ryan_mtg.servobot.model;

import com.ryan_mtg.servobot.data.factories.SerializerContainer;
import com.ryan_mtg.servobot.data.models.BotRow;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.HomeDelegatingListener;
import com.ryan_mtg.servobot.model.alerts.AlertQueue;
import com.ryan_mtg.servobot.model.scope.NullSymbolTable;
import com.ryan_mtg.servobot.model.scope.Scope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Bot {
    private static final Logger LOGGER = LoggerFactory.getLogger(Bot.class);
    private static final int MAX_NAME_SIZE = BotRow.MAX_NAME_SIZE;

    private String name;
    private Scope botScope;
    private BotEditor botEditor;
    private List<BotHome> homes = new ArrayList<>();
    private HomeDelegatingListener listener;
    private Map<Integer, Service> services;
    private Map<Integer, HomeEditor> homeEditorMap = new HashMap<>();
    private SerializerContainer serializers;
    private AlertQueue alertQueue = new AlertQueue(this);

    public Bot(final String name, final Scope globalScope, final Map<Integer, Service> services,
               final SerializerContainer serializers) throws BotErrorException {
        this.name = name;
        this.services = services;
        this.serializers = serializers;

        if (name.length() > MAX_NAME_SIZE) {
            throw new BotErrorException(String.format("Name too long (max %d): %s", MAX_NAME_SIZE, name));
        }

        botScope = new Scope(globalScope, new NullSymbolTable());
        botEditor = new BotEditor(this);
        listener = new HomeDelegatingListener(botEditor, homeEditorMap);
    }

    public String getName() {
        return name;
    }

    public BotEditor getBotEditor() {
        return botEditor;
    }

    public Scope getBotScope() {
        return botScope;
    }

    public Service getService(final int serviceType) {
        return services.get(serviceType);
    }

    public void addHome(final BotHome home) {
        homes.add(home);
        home.setBot(this);
        homeEditorMap.put(home.getId(), new HomeEditor(this, home));
        listener.register(home);
        services.values().stream().forEach(service -> service.register(home));
    }

    public void removeHome(final BotHome home) {
        home.stop(alertQueue);
        services.values().stream().forEach(service -> service.unregister(home));
        listener.unregister(home);
        homeEditorMap.remove(home.getId());
        homes.remove(home);
        home.setBot(null);
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

    public BotHome getHome(final String homeName) {
        for(BotHome home : homes) {
            if (home.getName().equals(homeName)) {
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

    public void startBot() throws Exception {
        for (Service service : services.values()) {
            service.start(listener);
        }

        homes.stream().forEach(home -> home.start(homeEditorMap.get(home.getId()), alertQueue));
        alertQueue.start();
        homes.stream().forEach(home -> alertQueue.scheduleAlert(home, Duration.ofSeconds(30), "startup"));
    }
}
