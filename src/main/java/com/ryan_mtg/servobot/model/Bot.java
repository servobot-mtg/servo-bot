package com.ryan_mtg.servobot.model;

import com.ryan_mtg.servobot.commands.CommandTable;
import com.ryan_mtg.servobot.commands.RateLimiter;
import com.ryan_mtg.servobot.data.factories.SerializerContainer;
import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.events.CommandPerformer;
import com.ryan_mtg.servobot.events.HomeDelegatingListener;
import com.ryan_mtg.servobot.model.alerts.Alert;
import com.ryan_mtg.servobot.model.alerts.AlertQueue;
import com.ryan_mtg.servobot.model.books.BookTable;
import com.ryan_mtg.servobot.model.game_queue.GameQueue;
import com.ryan_mtg.servobot.model.scope.Scope;
import com.ryan_mtg.servobot.model.storage.StorageTable;
import com.ryan_mtg.servobot.twitch.model.TwitchService;
import com.ryan_mtg.servobot.utility.Validation;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Bot implements Context {
    private static final Logger LOGGER = LoggerFactory.getLogger(Bot.class);

    @Getter
    private int id;

    @Getter
    private String name;

    @Getter
    private Scope botScope;

    @Getter
    private BotEditor botEditor;
    private List<BotHome> homes = new ArrayList<>();
    private HomeDelegatingListener listener;
    private Map<Integer, Service> services;
    private Map<Integer, HomeEditor> homeEditorMap = new HashMap<>();

    @Getter
    private SerializerContainer serializers;

    @Getter
    private CommandTable commandTable;

    @Getter
    private BookTable bookTable;

    @Getter
    private StorageTable storageTable;

    @Getter
    private AlertQueue alertQueue = new AlertQueue(this);

    public Bot(final int id, final String name, final Scope globalScope, final Map<Integer, Service> services,
            final SerializerContainer serializers, final CommandTable commandTable, final BookTable bookTable,
            final StorageTable storageTable) throws UserError {
        this.id = id;
        this.name = name;
        this.services = services;
        this.serializers = serializers;
        this.commandTable = commandTable;
        this.bookTable = bookTable;
        this.storageTable = storageTable;

        Validation.validateStringLength(name, Validation.MAX_NAME_LENGTH, "Name");

        botScope = new Scope(globalScope, bookTable);
        botEditor = new BotEditor(this);
        CommandPerformer commandPerformer = new CommandPerformer(new RateLimiter());
        listener = new HomeDelegatingListener(botEditor, homeEditorMap, commandPerformer, commandTable);
    }

    public Service getService(final int serviceType) {
        return services.get(serviceType);
    }

    @Override
    public int getContextId() {
        return -id;
    }

    @Override
    public Collection<Service> getServices() {
        return services.values();
    }

    @Override
    public String getImageUrl() {
        return getService(TwitchService.TYPE).getImageUrl();
    }

    @Override
    public Collection<GameQueue> getGameQueues() {
        return Collections.emptyList();
    }

    public void addHome(final BotHome home) {
        homes.add(home);
        home.setBot(this);
        homeEditorMap.put(home.getId(), new HomeEditor(this, home));
        listener.register(home);
        services.values().forEach(service -> service.register(home));
    }

    public void removeHome(final BotHome home) {
        home.stop(alertQueue, false);
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

    public void startBot() throws InterruptedException {
        startServices();

        homes.forEach(home -> home.start(homeEditorMap.get(home.getId()), alertQueue, false));
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
