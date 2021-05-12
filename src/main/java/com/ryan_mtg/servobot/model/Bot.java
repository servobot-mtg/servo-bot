package com.ryan_mtg.servobot.model;

import com.ryan_mtg.servobot.commands.CommandTable;
import com.ryan_mtg.servobot.commands.RateLimiter;
import com.ryan_mtg.servobot.data.factories.SerializerContainer;
import com.ryan_mtg.servobot.discord.model.DiscordService;
import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.events.CommandPerformer;
import com.ryan_mtg.servobot.events.HomeDelegatingListener;
import com.ryan_mtg.servobot.game.GameManager;
import com.ryan_mtg.servobot.model.alerts.Alert;
import com.ryan_mtg.servobot.model.alerts.AlertQueue;
import com.ryan_mtg.servobot.model.books.BookTable;
import com.ryan_mtg.servobot.model.game_queue.GameQueueTable;
import com.ryan_mtg.servobot.model.scope.Scope;
import com.ryan_mtg.servobot.model.storage.StorageTable;
import com.ryan_mtg.servobot.twitch.model.TwitchService;
import com.ryan_mtg.servobot.user.User;
import com.ryan_mtg.servobot.utility.Validation;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Bot implements Context {
    private static final Logger LOGGER = LoggerFactory.getLogger(Bot.class);

    @Getter
    private final int id;

    @Getter
    private final String name;

    @Getter
    private final Scope botScope;

    @Getter
    private final BotEditor botEditor;
    private final List<BotHome> homes = new ArrayList<>();
    private final HomeDelegatingListener listener;
    private final Map<Integer, Service> services;
    private final Map<Integer, HomeEditor> homeEditorMap = new HashMap<>();

    @Getter
    private final SerializerContainer serializers;

    @Getter
    private final CommandTable commandTable;

    @Getter
    private final BookTable bookTable;

    @Getter
    private final StorageTable storageTable;

    @Getter
    private final List<GameManager> gameManagers;

    @Getter
    private final AlertQueue alertQueue = new AlertQueue(this);

    public Bot(final int id, final String name, final Scope globalScope, final Map<Integer, Service> services,
            final SerializerContainer serializers, final CommandTable commandTable, final BookTable bookTable,
            final StorageTable storageTable, final List<GameManager> gameManagers) throws UserError {
        this.id = id;
        this.name = name;
        this.services = services;
        this.serializers = serializers;
        this.commandTable = commandTable;
        this.bookTable = bookTable;
        this.storageTable = storageTable;
        this.gameManagers = gameManagers;

        Validation.validateStringLength(name, Validation.MAX_NAME_LENGTH, "Name");

        botScope = new Scope(globalScope, bookTable);
        botEditor = new BotEditor(this);
        CommandPerformer commandPerformer = new CommandPerformer(new RateLimiter());
        listener = new HomeDelegatingListener(botEditor, homeEditorMap, commandPerformer, commandTable);

        gameManagers.forEach(gameManager -> gameManager.setResponder((user, message) -> {
            if (user.getDiscordId() != User.UNREGISTERED_ID) {
                botEditor.sendMessage(user, message, DiscordService.TYPE);
            }
        }));
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
    public GameQueueTable getGameQueueTable() {
        return new GameQueueTable();
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
        if (services.size() == 0) {
            return;
        }
        ExecutorService executor = Executors.newFixedThreadPool(services.size());

        for (Service service : services.values()) {
            executor.submit(() -> {
                try {
                    service.start(listener);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.MINUTES);
    }
}
