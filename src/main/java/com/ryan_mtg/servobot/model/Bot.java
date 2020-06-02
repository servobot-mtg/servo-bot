package com.ryan_mtg.servobot.model;

import com.ryan_mtg.servobot.commands.CommandTable;
import com.ryan_mtg.servobot.data.factories.SerializerContainer;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.HomeDelegatingListener;
import com.ryan_mtg.servobot.model.alerts.Alert;
import com.ryan_mtg.servobot.model.alerts.AlertQueue;
import com.ryan_mtg.servobot.model.books.BookTable;
import com.ryan_mtg.servobot.model.scope.Scope;
import com.ryan_mtg.servobot.utility.Validation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Bot {
    private static final Logger LOGGER = LoggerFactory.getLogger(Bot.class);

    private int id;
    private String name;
    private Scope botScope;
    private BotEditor botEditor;
    private List<BotHome> homes = new ArrayList<>();
    private HomeDelegatingListener listener;
    private Map<Integer, Service> services;
    private Map<Integer, HomeEditor> homeEditorMap = new HashMap<>();
    private SerializerContainer serializers;
    private CommandTable commandTable;
    private BookTable bookTable;
    private AlertQueue alertQueue = new AlertQueue(this);

    public Bot(final int id, final String name, final Scope globalScope, final Map<Integer, Service> services,
            final SerializerContainer serializers, final CommandTable commandTable, final BookTable bookTable)
            throws BotErrorException {
        this.id = id;
        this.name = name;
        this.services = services;
        this.serializers = serializers;
        this.commandTable = commandTable;
        this.bookTable = bookTable;

        Validation.validateStringLength(name, Validation.MAX_NAME_LENGTH, "Name");

        botScope = new Scope(globalScope, bookTable);
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

    public Map<Integer, Service> getServices() {
        return services;
    }

    public Service getService(final int serviceType) {
        return services.get(serviceType);
    }

    public void addHome(final BotHome home) {
        homes.add(home);
        home.setBot(this);
        homeEditorMap.put(home.getId(), new HomeEditor(this, home));
        listener.register(home);
        services.values().forEach(service -> service.register(home));
    }

    public void removeHome(final BotHome home) {
        home.stop(alertQueue);
        services.values().forEach(service -> service.unregister(home));
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

    public void startBot() throws InterruptedException {
        startServices();

        homes.forEach(home -> home.start(homeEditorMap.get(home.getId()), alertQueue));
        alertQueue.start();
        homes.forEach(home -> alertQueue.scheduleAlert(home, new Alert(Duration.ofSeconds(30), "startup")));
    }

    private void startServices() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(services.size());

        for (Service service : services.values()) {
            executor.submit(() -> {
                try {
                    service.start(listener);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.MINUTES);
    }
}
