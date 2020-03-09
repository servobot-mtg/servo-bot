package com.ryan_mtg.servobot.model;

import com.ryan_mtg.servobot.commands.CommandTable;
import com.ryan_mtg.servobot.discord.model.DiscordService;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.CommandListener;
import com.ryan_mtg.servobot.events.EventListener;
import com.ryan_mtg.servobot.events.MultiDelegatingListener;
import com.ryan_mtg.servobot.events.ReactionListener;
import com.ryan_mtg.servobot.model.alerts.AlertGenerator;
import com.ryan_mtg.servobot.model.alerts.AlertQueue;
import com.ryan_mtg.servobot.model.giveaway.Giveaway;
import com.ryan_mtg.servobot.model.reaction.ReactionTable;
import com.ryan_mtg.servobot.model.scope.BookScope;
import com.ryan_mtg.servobot.model.scope.FunctorSymbolTable;
import com.ryan_mtg.servobot.model.scope.Scope;
import com.ryan_mtg.servobot.model.storage.StorageTable;
import com.ryan_mtg.servobot.twitch.model.TwitchService;
import com.ryan_mtg.servobot.utility.Validation;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;


public class BotHome {
    private int id;
    private Bot bot;
    private String name;
    private String botName;
    private String timeZone;
    private Scope botHomeScope;
    private CommandTable commandTable;
    private ReactionTable reactionTable;
    private StorageTable storageTable;
    private Map<Integer, ServiceHome> serviceHomes;
    private List<Book> books;
    private List<GameQueue> gameQueues;
    private List<Giveaway> giveaways;
    private boolean active = false;
    private MultiDelegatingListener eventListener;

    public BotHome(final int id, final String name, final String botName, final String timeZone,
                   final CommandTable commandTable, final ReactionTable reactionTable, final StorageTable storageTable,
                   final Map<Integer, ServiceHome> serviceHomes, final List<Book> books,
                   final List<GameQueue> gameQueues, final List<Giveaway> giveaways) throws BotErrorException {
        this.id = id;
        this.name = name;
        this.botName = botName;
        this.timeZone = timeZone;
        this.commandTable = commandTable;
        this.reactionTable = reactionTable;
        this.storageTable = storageTable;
        this.serviceHomes = serviceHomes;
        this.books = books;
        this.gameQueues = gameQueues;
        this.giveaways = giveaways;

        Validation.validateStringLength(name, Validation.MAX_NAME_LENGTH, "Name");
        Validation.validateStringLength(botName, Validation.MAX_NAME_LENGTH, "Bot name");
        Validation.validateStringLength(timeZone, Validation.MAX_TIME_ZONE_LENGTH, "Time zone");

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
        if (bot == null) {
            botHomeScope = null;
        } else {
            botHomeScope = createScope(bot.getBotScope());
        }
    }

    public String getName() {
        return name;
    }

    public String getBotName() {
        return botName;
    }

    public String getImageUrl() {
        return getServiceHome(TwitchService.TYPE).getImageUrl();
    }

    public void setBotName(final String botName) {
        this.botName = botName;
        getServiceHome(DiscordService.TYPE).setName(botName);
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

    public Scope getBotHomeScope() {
        return botHomeScope;
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

    public List<Giveaway> getGiveaways() {
        return giveaways;
    }

    public Giveaway getGiveaway(final int giveawayId) {
        return giveaways.stream().filter(g -> g.getId() == giveawayId).findFirst().orElse(null);
    }

    public void addGiveaway(final Giveaway giveaway) {
        giveaways.add(giveaway);
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

    private Scope createScope(final Scope botScope) {
        FunctorSymbolTable timeSymbolTable = new FunctorSymbolTable();
        timeSymbolTable.addFunctor("year", () -> now().getYear());
        timeSymbolTable.addFunctor("month", () -> now().getMonthValue());
        timeSymbolTable.addFunctor("dayOfMonth", () -> now().getDayOfMonth());
        timeSymbolTable.addFunctor("dayOfYear", () -> now().getDayOfYear());
        timeSymbolTable.addFunctor("dayOfWeek", () -> now().getDayOfWeek());

        Scope timeScope = new Scope(botScope, timeSymbolTable);
        Scope bookScope = new Scope(timeScope, new BookScope(books));
        return new Scope(bookScope, storageTable);
    }

    private ZonedDateTime now() {
        ZoneId zoneId = ZoneId.of(timeZone);
        return ZonedDateTime.now(zoneId);
    }
}
