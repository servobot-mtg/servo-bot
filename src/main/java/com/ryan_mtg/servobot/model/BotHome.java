package com.ryan_mtg.servobot.model;

import com.ryan_mtg.servobot.commands.CommandTable;
import com.ryan_mtg.servobot.events.CommandListener;
import com.ryan_mtg.servobot.events.EventListener;
import com.ryan_mtg.servobot.events.MultiDelegatingListener;
import com.ryan_mtg.servobot.events.ReactionListener;
import com.ryan_mtg.servobot.model.alerts.AlertGenerator;
import com.ryan_mtg.servobot.model.alerts.AlertQueue;
import com.ryan_mtg.servobot.model.reaction.ReactionTable;
import com.ryan_mtg.servobot.model.storage.StorageTable;

import java.util.List;
import java.util.Map;

public class BotHome {
    private int id;
    private Bot bot;
    private String name;
    private String timeZone;
    private CommandTable commandTable;
    private ReactionTable reactionTable;
    private StorageTable storageTable;
    private Map<Integer, ServiceHome> serviceHomes;
    private List<Book> books;
    private List<GameQueue> gameQueues;
    private boolean active = false;
    private MultiDelegatingListener eventListener;

    public BotHome(final int id, final String name, final String timeZone,
                   final CommandTable commandTable, final ReactionTable reactionTable, final StorageTable storageTable,
                   final Map<Integer, ServiceHome> serviceHomes, final List<Book> books,
                   final List<GameQueue> gameQueues) {
        this.id = id;
        this.name = name;
        this.timeZone = timeZone;
        this.commandTable = commandTable;
        this.reactionTable = reactionTable;
        this.storageTable = storageTable;
        this.serviceHomes = serviceHomes;
        this.books = books;
        this.gameQueues = gameQueues;

        reactionTable.setTimeZone(timeZone);
        commandTable.setTimeZone(timeZone);
        eventListener =
                new MultiDelegatingListener(new CommandListener(commandTable), new ReactionListener(reactionTable));
    }

    public int getId() {
        return id;
    }

    public Bot getBot() {
        return bot;
    }

    public void setBot(final Bot bot) {
        this.bot = bot;
    }

    public String getName() {
        return name;
    }

    public boolean isActive() {
        return active;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(final String timeZone) {
        this.timeZone = timeZone;
    }

    public CommandTable getCommandTable() {
        return commandTable;
    }

    public ReactionTable getReactionTable() {
        return reactionTable;
    }

    public StorageTable getStorageTable() {
        return storageTable;
    }

    public EventListener getListener() {
        return eventListener;
    }

    public Map<Integer, ServiceHome> getServiceHomes() {
        return serviceHomes;
    }

    public ServiceHome getServiceHome(final int serviceType) {
        return serviceHomes.get(serviceType);
    }

    public List<Book> getBooks() {
        return books;
    }

    public List<AlertGenerator> getAlertGenerators() {
        return commandTable.getAlertGenerators();
    }

    public List<GameQueue> getGameQueues() {
        return gameQueues;
    }

    public GameQueue getGameQueue(final int gameQueueId) {
        return gameQueues.stream().filter(gameQueue -> gameQueue.getId() == gameQueueId).findFirst().orElse(null);
    }

    public void start(final HomeEditor homeEditor, final AlertQueue alertQueue) {
        serviceHomes.values().forEach(serviceHome -> {
            serviceHome.setHomeEditor(homeEditor);
            serviceHome.start(this);
        });
        alertQueue.update(this);
        active = true;
        eventListener.setActive(active);
    }

    public void stop(final AlertQueue alertQueue) {
        if (active) {
            active = false;
            eventListener.setActive(active);
            serviceHomes.values().forEach(serviceHome -> serviceHome.stop(this));
            alertQueue.remove(this);
        }
    }
}
