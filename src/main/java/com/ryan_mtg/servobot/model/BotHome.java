package com.ryan_mtg.servobot.model;

import com.ryan_mtg.servobot.commands.CommandTable;
import com.ryan_mtg.servobot.commands.RateLimiter;
import com.ryan_mtg.servobot.discord.model.DiscordService;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.CommandListener;
import com.ryan_mtg.servobot.events.CommandPerformer;
import com.ryan_mtg.servobot.events.MultiDelegatingListener;
import com.ryan_mtg.servobot.events.ReactionListener;
import com.ryan_mtg.servobot.model.alerts.AlertGenerator;
import com.ryan_mtg.servobot.model.alerts.AlertQueue;
import com.ryan_mtg.servobot.model.books.BookTable;
import com.ryan_mtg.servobot.model.game_queue.GameQueue;
import com.ryan_mtg.servobot.model.giveaway.Giveaway;
import com.ryan_mtg.servobot.model.reaction.ReactionTable;
import com.ryan_mtg.servobot.model.scope.SimpleSymbolTable;
import com.ryan_mtg.servobot.model.scope.Scope;
import com.ryan_mtg.servobot.model.storage.StorageTable;
import com.ryan_mtg.servobot.twitch.model.TwitchService;
import com.ryan_mtg.servobot.user.HomedUser;
import com.ryan_mtg.servobot.user.HomedUserTable;
import com.ryan_mtg.servobot.utility.Strings;
import com.ryan_mtg.servobot.utility.Validation;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class BotHome {
    private static final Logger LOGGER = LoggerFactory.getLogger(BotHome.class);
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("H:mm");

    @Getter
    private int id;

    @Getter
    private Bot bot;

    @Getter
    private String name;

    @Getter
    private String botName;

    @Getter @Setter
    private String timeZone;

    @Getter
    private Scope botHomeScope;

    @Getter
    private HomedUserTable homedUserTable;

    @Getter
    private CommandTable commandTable;

    @Getter
    private BookTable bookTable;

    @Getter
    private ReactionTable reactionTable;

    @Getter
    private StorageTable storageTable;

    @Getter
    private Map<Integer, ServiceHome> serviceHomes;

    @Getter
    private List<GameQueue> gameQueues;

    @Getter
    private List<Giveaway> giveaways;

    @Getter
    private boolean active = false;

    @Getter
    private MultiDelegatingListener eventListener;

    public BotHome(final int id, final String name, final String botName, final String timeZone,
                   final HomedUserTable homedUserTable, final BookTable bookTable, final CommandTable commandTable,
                   final ReactionTable reactionTable, final StorageTable storageTable,
                   final Map<Integer, ServiceHome> serviceHomes, final List<GameQueue> gameQueues,
                   final List<Giveaway> giveaways) throws BotErrorException {
        this.id = id;
        this.name = name;
        this.botName = botName;
        this.timeZone = timeZone;
        this.homedUserTable = homedUserTable;
        this.bookTable = bookTable;
        this.commandTable = commandTable;
        this.reactionTable = reactionTable;
        this.storageTable = storageTable;
        this.serviceHomes = serviceHomes;
        this.gameQueues = gameQueues;
        this.giveaways = giveaways;

        Validation.validateStringLength(name, Validation.MAX_NAME_LENGTH, "Name");
        Validation.validateStringLength(botName, Validation.MAX_NAME_LENGTH, "Bot name");
        Validation.validateStringLength(timeZone, Validation.MAX_TIME_ZONE_LENGTH, "Time zone");

        reactionTable.setTimeZone(timeZone);
        commandTable.setTimeZone(timeZone);
        CommandPerformer commandPerformer = new CommandPerformer(new RateLimiter());
        eventListener =
                new MultiDelegatingListener(new CommandListener(commandPerformer, commandTable),
                        new ReactionListener(reactionTable, commandPerformer));
    }

    public void setBot(final Bot bot) {
        this.bot = bot;
        if (bot == null) {
            botHomeScope = null;
        } else {
            botHomeScope = createScope(bot.getBotScope());
        }
    }

    public void setBotName(final String botName) {
        this.botName = botName;
        getServiceHome(DiscordService.TYPE).setName(botName);
    }

    public String getImageUrl() {
        return getServiceHome(TwitchService.TYPE).getImageUrl();
    }

    public ServiceHome getServiceHome(final int serviceType) {
        return serviceHomes.get(serviceType);
    }

    public List<AlertGenerator> getAlertGenerators() {
        return commandTable.getAlertGenerators();
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
        eventListener.setActive(true);
    }

    public void registerAlertGenerator(final AlertGenerator alertGenerator) {
        if (active) {
            bot.getAlertQueue().add(this, alertGenerator);
        }
    }

    public void stop(final AlertQueue alertQueue) {
        if (active) {
            active = false;
            eventListener.setActive(false);
            serviceHomes.values().forEach(serviceHome -> serviceHome.stop(this));
            alertQueue.remove(this);
        }
    }

    private Scope createScope(final Scope botScope) {
        SimpleSymbolTable timeSymbolTable = new SimpleSymbolTable();
        timeSymbolTable.addFunctor("year", () -> now().getYear());
        timeSymbolTable.addFunctor("month", () -> now().getMonthValue());
        timeSymbolTable.addFunctor("dayOfMonth", () -> now().getDayOfMonth());
        timeSymbolTable.addFunctor("dayOfYear", () -> now().getDayOfYear());
        timeSymbolTable.addFunctor("dayOfWeek",
                () -> Strings.capitalize(now().getDayOfWeek().toString().toLowerCase()));
        timeSymbolTable.addFunctor("timeOfDay", () -> dateTimeFormatter.format(now()));

        timeSymbolTable.addValue("hasUser", (Function<String, Boolean>)this::hasUser);
        timeSymbolTable.addValue("findUser", (Function<String, HomedUser>)this::findUser);

        Scope timeScope = new Scope(botScope, timeSymbolTable);
        Scope bookScope = new Scope(timeScope, bookTable);
        return new Scope(bookScope, storageTable);
    }

    private ZonedDateTime now() {
        ZoneId zoneId = ZoneId.of(timeZone);
        return ZonedDateTime.now(zoneId);
    }

    private boolean hasUser(final String userName) {
        for (ServiceHome serviceHome : serviceHomes.values()) {
            if (serviceHome.getHome().hasUser(userName)) {
                return true;
            }
        }
        return false;
    }

    private HomedUser findUser(final String userName) {
        try {
            for (ServiceHome serviceHome : serviceHomes.values()) {
                Home home = serviceHome.getHome();
                if (home.hasUser(userName)) {
                    return home.getUser(userName).getHomedUser();
                }
            }
        } catch (BotErrorException e) {
            LOGGER.error(String.format("Problem finding user %s", userName), e);
        }
        return null;
    }
}
