package com.ryan_mtg.servobot.model;

import com.ryan_mtg.servobot.commands.hierarchy.CommandSettings;
import com.ryan_mtg.servobot.commands.hierarchy.Command;
import com.ryan_mtg.servobot.commands.hierarchy.RateLimit;
import com.ryan_mtg.servobot.commands.trigger.CommandAlert;
import com.ryan_mtg.servobot.commands.CommandTable;
import com.ryan_mtg.servobot.commands.CommandTableEdit;
import com.ryan_mtg.servobot.commands.giveaway.EnterRaffleCommand;
import com.ryan_mtg.servobot.commands.chat.MessageChannelCommand;
import com.ryan_mtg.servobot.commands.giveaway.RaffleStatusCommand;
import com.ryan_mtg.servobot.commands.Permission;
import com.ryan_mtg.servobot.commands.giveaway.SelectWinnerCommand;
import com.ryan_mtg.servobot.commands.giveaway.StartRaffleCommand;
import com.ryan_mtg.servobot.commands.trigger.Trigger;
import com.ryan_mtg.servobot.controllers.CommandDescriptor;
import com.ryan_mtg.servobot.data.factories.SerializerContainer;
import com.ryan_mtg.servobot.data.models.AlertGeneratorRow;
import com.ryan_mtg.servobot.data.models.BotHomeRow;
import com.ryan_mtg.servobot.data.models.CommandRow;
import com.ryan_mtg.servobot.data.models.SuggestionRow;
import com.ryan_mtg.servobot.data.repositories.BotHomeRepository;
import com.ryan_mtg.servobot.data.repositories.SuggestionRepository;
import com.ryan_mtg.servobot.discord.model.DiscordService;
import com.ryan_mtg.servobot.events.AlertEvent;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.BotHomeAlertEvent;
import com.ryan_mtg.servobot.model.alerts.Alert;
import com.ryan_mtg.servobot.model.alerts.AlertGenerator;
import com.ryan_mtg.servobot.model.books.Book;
import com.ryan_mtg.servobot.model.books.BookTable;
import com.ryan_mtg.servobot.model.books.BookTableEdit;
import com.ryan_mtg.servobot.model.books.Statement;
import com.ryan_mtg.servobot.model.editors.BookTableEditor;
import com.ryan_mtg.servobot.model.editors.CommandTableEditor;
import com.ryan_mtg.servobot.model.editors.StorageValueEditor;
import com.ryan_mtg.servobot.model.game_queue.GameQueue;
import com.ryan_mtg.servobot.model.game_queue.GameQueueEntry;
import com.ryan_mtg.servobot.model.giveaway.GiveawayCommandSettings;
import com.ryan_mtg.servobot.model.giveaway.Giveaway;
import com.ryan_mtg.servobot.model.giveaway.GiveawayEdit;
import com.ryan_mtg.servobot.model.giveaway.Prize;
import com.ryan_mtg.servobot.model.giveaway.Raffle;
import com.ryan_mtg.servobot.model.giveaway.RaffleSettings;
import com.ryan_mtg.servobot.model.reaction.AlwaysReact;
import com.ryan_mtg.servobot.model.reaction.Pattern;
import com.ryan_mtg.servobot.model.reaction.Reaction;
import com.ryan_mtg.servobot.model.reaction.ReactionTable;
import com.ryan_mtg.servobot.model.reaction.ReactionTableEdit;
import com.ryan_mtg.servobot.model.scope.Scope;
import com.ryan_mtg.servobot.model.storage.IntegerStorageValue;
import com.ryan_mtg.servobot.model.storage.StorageTable;
import com.ryan_mtg.servobot.model.storage.StorageValue;
import com.ryan_mtg.servobot.twitch.model.TwitchService;
import com.ryan_mtg.servobot.twitch.model.TwitchServiceHome;
import com.ryan_mtg.servobot.user.HomedUser;
import com.ryan_mtg.servobot.user.User;
import com.ryan_mtg.servobot.utility.Strings;
import com.ryan_mtg.servobot.utility.Validation;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.transaction.Transactional;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.ryan_mtg.servobot.model.game_queue.GameQueue.EMPTY_QUEUE;

public class HomeEditor {
    private static Logger LOGGER = LoggerFactory.getLogger(HomeEditor.class);
    private Bot bot;
    private BotHome botHome;
    private SerializerContainer serializers;

    @Getter
    private CommandTableEditor commandTableEditor;

    @Getter
    private BookTableEditor bookTableEditor;

    @Getter
    private StorageValueEditor storageValueEditor;

    private static final String REQUEST_PRIZE_DESCRIPTION = "Request prize command name";

    public HomeEditor(final Bot bot, final BotHome botHome) {
        this.bot = bot;
        this.botHome = botHome;
        this.serializers = bot.getSerializers();
        this.commandTableEditor = new CommandTableEditor(botHome.getBookTable(), botHome.getCommandTable(),
                serializers.getCommandSerializer(), serializers.getCommandTableSerializer());
        this.bookTableEditor =
                new BookTableEditor(botHome.getId(), botHome.getBookTable(), serializers.getBookSerializer());
        this.storageValueEditor =new StorageValueEditor(botHome.getId(), botHome.getStorageTable(),
                serializers.getStorageValueSerializer());
    }

    public Scope getScope() {
        return botHome.getBotHomeScope();
    }

    public Service getService(final int serviceType) {
        return bot.getService(serviceType);
    }

    @Transactional(rollbackOn = BotErrorException.class)
    public void modifyBotName(final String botName) {
        botHome.setBotName(botName);

        BotHomeRepository botHomeRepository = serializers.getBotHomeRepository();
        BotHomeRow botHomeRow = botHomeRepository.findById(botHome.getId());
        botHomeRow.setBotName(botName);
        botHomeRepository.save(botHomeRow);
    }

    @Transactional(rollbackOn = BotErrorException.class)
    public void setTimeZone(final String timeZone) {
        botHome.setTimeZone(timeZone);
        botHome.getReactionTable().setTimeZone(timeZone);
        botHome.getCommandTable().setTimeZone(timeZone);

        bot.getAlertQueue().update(botHome);

        BotHomeRepository botHomeRepository = serializers.getBotHomeRepository();
        BotHomeRow botHomeRow = botHomeRepository.findById(botHome.getId());
        botHomeRow.setTimeZone(timeZone);
        botHomeRepository.save(botHomeRow);
    }

    public HomedUser getUserById(final int userId) throws BotErrorException {
        return botHome.getHomedUserTable().getById(userId);
    }

    public HomedUser getUserByDiscordId(final long discordId, final String discordUsername) throws BotErrorException {
        return botHome.getHomedUserTable().getByDiscordId(discordId, discordUsername);
    }

    public HomedUser getUserByTwitchId(final int twitchId, final String twitchUsername) throws BotErrorException {
        return botHome.getHomedUserTable().getByTwitchId(twitchId, twitchUsername);
    }

    @Transactional(rollbackOn = BotErrorException.class)
    public Reaction addReaction(final String emote, final boolean secure) throws BotErrorException {
        Reaction  reaction = new Reaction(Reaction.UNREGISTERED_ID, emote, secure, new AlwaysReact(), new ArrayList<>(),
                new ArrayList<>());
        ReactionTable reactionTable = botHome.getReactionTable();
        ReactionTableEdit reactionTableEdit = reactionTable.addReaction(reaction);
        serializers.getReactionTableSerializer().commit(botHome.getId(), reactionTableEdit);
        return reaction;
    }

    @Transactional(rollbackOn = BotErrorException.class)
    public boolean secureReaction(final int reactionId, final boolean secure) {
        Reaction reaction = botHome.getReactionTable().secureReaction(reactionId, secure);
        serializers.getReactionSerializer().saveReaction(botHome.getId(), reaction);
        return reaction.isSecure();
    }

    @Transactional(rollbackOn = BotErrorException.class)
    public void deleteReaction(final int reactionId) {
        ReactionTableEdit reactionTableEdit = botHome.getReactionTable().deleteReaction(reactionId);
        serializers.getReactionTableSerializer().commit(botHome.getId(), reactionTableEdit);
    }

    @Transactional(rollbackOn = BotErrorException.class)
    public Pattern addPattern(final int reactionId, final String pattern) throws BotErrorException {
        ReactionTableEdit reactionTableEdit = botHome.getReactionTable().addPattern(reactionId, pattern);
        serializers.getReactionTableSerializer().commit(botHome.getId(), reactionTableEdit);
        return reactionTableEdit.getSavedPatterns().keySet().iterator().next();
    }

    @Transactional(rollbackOn = BotErrorException.class)
    public void deletePattern(final int reactionId, final int patternId) {
        ReactionTableEdit reactionTableEdit = botHome.getReactionTable().deletePattern(reactionId, patternId);
        serializers.getReactionTableSerializer().commit(botHome.getId(), reactionTableEdit);
    }

    @Transactional(rollbackOn = BotErrorException.class)
    public Book addBook(final String name, final String text) throws BotErrorException {
        Book book = new Book(Book.UNREGISTERED_ID, name);
        BookTable bookTable = botHome.getBookTable();
        BookTableEdit bookTableEdit = bookTable.addBook(book);
        if (!Strings.isBlank(text)) {
            Statement statement = new Statement(Statement.UNREGISTERED_ID, text);
            bookTableEdit.merge(bookTable.addStatement(book, statement));
        }
        serializers.getBookSerializer().commit(botHome.getId(), bookTableEdit);
        return book;
    }

    @Transactional(rollbackOn = BotErrorException.class)
    public Statement addStatement(final int bookId, final String text) throws BotErrorException {
        Statement statement = new Statement(Statement.UNREGISTERED_ID, text);
        BookTableEdit bookTableEdit = botHome.getBookTable().addStatement(bookId, statement);
        serializers.getBookSerializer().commit(botHome.getId(), bookTableEdit);
        return statement;
    }

    public Optional<Book> getBook(final String bookName) {
        return botHome.getBookTable().getBook(bookName);
    }

    @Transactional(rollbackOn = BotErrorException.class)
    public void deleteStatement(final int bookId, final int statementId) throws BotErrorException {
        BookTableEdit bookTableEdit = botHome.getBookTable().deleteStatement(bookId, statementId);
        serializers.getBookSerializer().commit(botHome.getId(), bookTableEdit);
    }

    @Transactional(rollbackOn = BotErrorException.class)
    public void modifyStatement(final int bookId, final int statementId, final String text) throws BotErrorException {
        BookTableEdit bookTableEdit = new BookTableEdit();
        Book book = botHome.getBookTable().getBook(bookId);
        Statement statement = book.getStatement(statementId);
        statement.setText(text);
        bookTableEdit.save(bookId, statement);
        serializers.getBookSerializer().commit(botHome.getId(), bookTableEdit);
    }

    public void scheduleAlert(final Alert alert) {
        bot.getAlertQueue().scheduleAlert(botHome, alert);
    }

    public void alert(final String alertToken) {
        Home home = new MultiServiceHome(botHome.getServiceHomes(), this);
        AlertEvent alertEvent = new BotHomeAlertEvent(botHome, alertToken, home);
        botHome.getEventListener().onAlert(alertEvent);
    }

    @Transactional(rollbackOn = BotErrorException.class)
    public AlertGenerator addAlert(final int type, final String keyword, final int time) throws BotErrorException {
        Validation.validateStringValue(keyword, Validation.MAX_TRIGGER_LENGTH, "Alert Keyword",
                Validation.NAME_PATTERN);

        Validation.validateRange(time, "Time", 1, 24 * 60 * 60);

        AlertGeneratorRow alertGeneratorRow = new AlertGeneratorRow();
        alertGeneratorRow.setId(AlertGenerator.UNREGISTERED_ID);
        alertGeneratorRow.setBotHomeId(botHome.getId());
        alertGeneratorRow.setType(type);
        alertGeneratorRow.setAlertToken(keyword);
        alertGeneratorRow.setTime(time);
        AlertGenerator alertGenerator =
                serializers.getAlertGeneratorSerializer().createAlertGenerator(alertGeneratorRow);

        alertGenerator.setTimeZone(botHome.getTimeZone());

        CommandTableEdit commandTableEdit = botHome.getCommandTable().addAlertGenerator(alertGenerator);
        serializers.getCommandTableSerializer().commit(commandTableEdit);
        botHome.registerAlertGenerator(alertGenerator);
        return alertGenerator;
    }

    @Transactional(rollbackOn = BotErrorException.class)
    public void deleteAlert(final int alertGeneratorId) {
        CommandTableEdit commandTableEdit = botHome.getCommandTable().deleteAlertGenerator(alertGeneratorId);
        serializers.getCommandTableSerializer().commit(commandTableEdit);
    }

    @Transactional(rollbackOn = BotErrorException.class)
    public void addSuggestion(final String command) {
        String alias = command.toLowerCase();
        if (alias.length() > Validation.MAX_TRIGGER_LENGTH) {
            return; //ignore suggestions that are absurd
        }

        SuggestionRepository suggestionRepository = serializers.getSuggestionRepository();
        SuggestionRow suggestionRow = suggestionRepository.findByAlias(alias);
        if (suggestionRow == null) {
            suggestionRow = new SuggestionRow(alias, 1);
        } else {
            suggestionRow.setCount(suggestionRow.getCount() + 1);
        }
        suggestionRepository.save(suggestionRow);
    }

    public StorageValue getStorageValue(final String name) throws BotErrorException {
        StorageValue.validateName(name);
        StorageValue storageValue = botHome.getStorageTable().getStorage(name);
        if (storageValue == null) {
            throw new BotErrorException(String.format("No value with name %s.", name));
        }
        return storageValue;
    }

    public List<StorageValue> getAllUsersStorageValues(final String name) throws BotErrorException {
        StorageValue.validateName(name);
        return botHome.getStorageTable().getAllUsersStorage(name);
    }

    public IntegerStorageValue getStorageValue(final int userId, final String name, final int defaultValue)
            throws BotErrorException {
        StorageValue.validateName(name);
        StorageTable storageTable = botHome.getStorageTable();
        StorageValue storageValue = storageTable.getStorage(userId, name);
        if (storageValue == null) {
            IntegerStorageValue newValue =
                    new IntegerStorageValue(StorageValue.UNREGISTERED_ID, userId, name, defaultValue);
            storageTable.registerValue(newValue);
            return newValue;
        }
        if (storageValue instanceof IntegerStorageValue) {
            return (IntegerStorageValue) storageValue;
        }

        throw new BotErrorException(String.format("%s is not a number", name));
    }

    @Transactional(rollbackOn = BotErrorException.class)
    public StorageValue setStorageValue(final String name, final String value) throws BotErrorException {
        StorageValue storageValue = getStorageValue(name);
        if (storageValue instanceof IntegerStorageValue) {
            IntegerStorageValue integerValue = (IntegerStorageValue) storageValue;
            try {
                integerValue.setValue(Integer.parseInt(value));
            } catch (Exception e) {
                throw new BotErrorException(String.format("Invalid value %s.", value));
            }
            serializers.getStorageValueSerializer().save(integerValue, botHome.getId());
        } else {
            throw new BotErrorException(String.format("%s has an unknown type of value.", storageValue.getName()));
        }
        return storageValue;
    }

    @Transactional(rollbackOn = BotErrorException.class)
    public void removeStorageVariables(final String name) {
        botHome.getStorageTable().removeVariables(name);
        serializers.getStorageTableSerializer().removeVariables(name, botHome.getId());
    }

    @Transactional(rollbackOn = BotErrorException.class)
    public void removeStorageVariable(final int userId, final String name) {
        StorageValue storageValue = botHome.getStorageTable().removeVariable(userId, name);
        if (storageValue != null) {
            serializers.getStorageValueRepository().deleteById(storageValue.getId());
        }
    }

    @Transactional(rollbackOn = BotErrorException.class)
    public IntegerStorageValue incrementStorageValue(final String name) throws BotErrorException {
        StorageValue value = getStorageValue(name);
        if (value instanceof IntegerStorageValue) {
            IntegerStorageValue integerValue = (IntegerStorageValue) value;
            integerValue.setValue(integerValue.getValue() + 1);
            serializers.getStorageValueSerializer().save(integerValue, botHome.getId());
            return integerValue;
        }
        throw new BotErrorException(String.format("%s is not a number", name));
    }

    @Transactional(rollbackOn = BotErrorException.class)
    public IntegerStorageValue incrementStorageValue(final int userId, final String name, final int defaultValue)
            throws BotErrorException {
        return increaseStorageValue(userId, name, 1, defaultValue);
    }

    @Transactional(rollbackOn = BotErrorException.class)
    public IntegerStorageValue increaseStorageValue(final int userId, final String name, final int amount,
            final int defaultValue) throws BotErrorException {
        IntegerStorageValue value = getStorageValue(userId, name, defaultValue);
        value.setValue(value.getValue() + amount);
        serializers.getStorageValueSerializer().save(value, botHome.getId());
        return value;
    }

    @Transactional(rollbackOn = BotErrorException.class)
    public String startGameQueue(final int gameQueueId, final String name) throws BotErrorException {
        GameQueue gameQueue = getGameQueue(gameQueueId);

        if (gameQueue.getState() == GameQueue.State.PLAYING) {
            if (name != null) {
                setGameQueueName(gameQueueId, name);
                return String.format("Game queue name changed to '%s.'", name);
            }
            throw new  BotErrorException(String.format("Game queue '%s' already started.", gameQueue.getName()));
        }

        gameQueue.setState(GameQueue.State.PLAYING);
        if (name != null) {
            gameQueue.setName(name);
        }

        serializers.getGameQueueSerializer().saveGameQueue(gameQueue);
        return String.format("Game queue '%s' started.", gameQueue.getName());
    }

    @Transactional(rollbackOn = BotErrorException.class)
    public String closeGameQueue(final int gameQueueId) throws BotErrorException {
        GameQueue gameQueue = getGameQueue(gameQueueId);

        if (gameQueue.getState() == GameQueue.State.CLOSED) {
            throw new BotErrorException(String.format("Game queue '%s' is already closed.", gameQueue.getName()));
        }

        if (gameQueue.getState() != GameQueue.State.PLAYING) {
            throw new BotErrorException(String.format("Game queue '%s' is not open.", gameQueue.getName()));
        }

        gameQueue.setState(GameQueue.State.CLOSED);

        serializers.getGameQueueSerializer().saveGameQueue(gameQueue);
        return String.format("Queue '%s' is now closed.", gameQueue.getName());
    }

    @Transactional(rollbackOn = BotErrorException.class)
    public User popGameQueue(final int gameQueueId) throws BotErrorException {
        GameQueue gameQueue = getGameQueue(gameQueueId);
        if (gameQueue.getState() == GameQueue.State.IDLE) {
            throw new BotErrorException(String.format("Game queue '%s' is not active.", gameQueue.getName()));
        }

        int nextPlayer = gameQueue.pop();
        if (nextPlayer == EMPTY_QUEUE) {
            throw new BotErrorException("No players in the queue.");
        }

        serializers.getGameQueueSerializer().removeEntry(gameQueue, nextPlayer);
        return serializers.getUserTable().getById(gameQueue.getCurrentPlayerId());
    }

    public User peekGameQueue(final int gameQueueId) throws BotErrorException {
        GameQueue gameQueue = getGameQueue(gameQueueId);
        if (gameQueue.getState() == GameQueue.State.IDLE) {
            throw new BotErrorException(String.format("Game queue '%s' is not active.", gameQueue.getName()));
        }

        int nextPlayer = gameQueue.getCurrentPlayerId();
        if (nextPlayer == EMPTY_QUEUE) {
            throw new BotErrorException("No players in the queue.");
        }

        return serializers.getUserTable().getById(gameQueue.getCurrentPlayerId());
    }

    @Transactional(rollbackOn = BotErrorException.class)
    public int joinGameQueue(final int gameQueueId, final com.ryan_mtg.servobot.model.User player)
            throws BotErrorException {
        GameQueue gameQueue = getGameQueue(gameQueueId);

        int playerId = player.getId();

        if (gameQueue.contains(playerId)) {
            throw new BotErrorException(String.format("%s is already in the queue.", player.getName()));
        }

        if (gameQueue.getState() == GameQueue.State.IDLE) {
            throw new BotErrorException(String.format("Game queue '%s' is not active.", gameQueue.getName()));
        }
        if (gameQueue.getState() == GameQueue.State.CLOSED) {
            throw new BotErrorException(String.format("Game queue '%s' is not allowing new players.",
                    gameQueue.getName()));
        }

        GameQueueEntry gameQueueEntry = gameQueue.enqueue(playerId);

        serializers.getGameQueueSerializer().addEntry(gameQueue, gameQueueEntry);
        return gameQueueEntry.getPosition();
    }

    @Transactional(rollbackOn = BotErrorException.class)
    public void removeFromGameQueue(final int gameQueueId, final com.ryan_mtg.servobot.model.User player)
            throws BotErrorException {
        GameQueue gameQueue = getGameQueue(gameQueueId);

        int playerId = player.getId();

        if (gameQueue.getState() == GameQueue.State.IDLE) {
            throw new BotErrorException(String.format("Game queue '%s' is not active.", gameQueue.getName()));
        }

        if (gameQueue.getCurrentPlayerId() == playerId) {
            throw new BotErrorException(String.format("%s is currently playing.", player.getName()));
        }

        if (!gameQueue.contains(playerId)) {
            throw new BotErrorException(String.format("%s is not in the queue.", player.getName()));
        }

        gameQueue.remove(playerId);
        serializers.getGameQueueSerializer().removeEntry(gameQueue, playerId);
    }

    @Transactional(rollbackOn = BotErrorException.class)
    public void setGameQueueName(final int gameQueueId, final String name) throws BotErrorException {
        GameQueue gameQueue = getGameQueue(gameQueueId);

        if (name.equals(gameQueue.getName())) {
            return;
        }

        gameQueue.setName(name);

        serializers.getGameQueueSerializer().saveGameQueue(gameQueue);
    }

    @Transactional(rollbackOn = BotErrorException.class)
    public String stopGameQueue(int gameQueueId) throws BotErrorException {
        GameQueue gameQueue = getGameQueue(gameQueueId);
        if (gameQueue.getState() == GameQueue.State.IDLE) {
            throw new BotErrorException(String.format("Game queue '%s' has already been stopped.",
                    gameQueue.getName()));
        }

        gameQueue.setState(GameQueue.State.IDLE);

        serializers.getGameQueueSerializer().emptyGameQueue(gameQueue);
        return String.format("Game queue '%s' stopped.", gameQueue.getName());
    }

    @Transactional(rollbackOn = BotErrorException.class)
    public String showGameQueue(final int gameQueueId) throws BotErrorException {
        GameQueue gameQueue = getGameQueue(gameQueueId);

        if (gameQueue.getState() == GameQueue.State.IDLE) {
            return String.format("Game queue '%s' has been stopped.", gameQueue.getName());
        }

        StringBuilder response = new StringBuilder();

        response.append("Game queue '").append(gameQueue.getName()).append('\'');

        if (gameQueue.getState() == GameQueue.State.CLOSED) {
            response.append(" is not accepting new players.");
        } else {
            response.append(".");
        }

        int currentPlayerId = gameQueue.getCurrentPlayerId();
        if (currentPlayerId != EMPTY_QUEUE) {
            response.append(" The current player is ").append(describePlayer(currentPlayerId)).append('.');
        }

        List<GameQueueEntry> queue = gameQueue.getFullQueue();
        if (queue.isEmpty()) {
            response.append(" The queue is empty.");
        } else {
            for (int i = 0; i < 5 && i < queue.size(); i++) {
                GameQueueEntry entry = queue.get(i);
                response.append(" (").append(entry.getPosition()).append(") ");
                response.append(describePlayer(entry.getUserId()));
            }
        }

        return response.toString();
    }

    public Giveaway getGiveaway(final int giveawayId) {
        return botHome.getGiveaway(giveawayId);
    }

    @Transactional(rollbackOn = BotErrorException.class)
    public Giveaway addGiveaway(final String name, final boolean selfService, final boolean raffle)
            throws BotErrorException {
        Giveaway giveaway = new Giveaway(Giveaway.UNREGISTERED_ID, name, selfService, raffle);
        serializers.getGiveawaySerializer().saveGiveaway(botHome.getId(), giveaway);
        botHome.addGiveaway(giveaway);

        return giveaway;
    }

    @Transactional(rollbackOn = BotErrorException.class)
    public Giveaway saveGiveawaySelfService(final int giveawayId, final String requestPrizeCommandName,
            final int prizeRequestLimit, final int prizeRequestUserLimit) throws BotErrorException {
        Giveaway giveaway = getGiveaway(giveawayId);
        if (giveaway.getState() != Giveaway.State.CONFIGURING) {
            throw new BotErrorException("Can only save configuration when giveaway in in configuring state");
        }

        CommandTable commandTable = botHome.getCommandTable();
        Validation.validateSetTemporaryCommandName(requestPrizeCommandName, giveaway.getRequestPrizeCommandName(),
                commandTable, true, REQUEST_PRIZE_DESCRIPTION);

        Validation.validateRange(prizeRequestLimit, "Prize request limit", 1, 1000);
        Validation.validateRange(prizeRequestUserLimit, "Prize request limit per user", 1, 10);

        giveaway.setRequestPrizeCommandName(requestPrizeCommandName);
        giveaway.setPrizeRequestLimit(prizeRequestLimit);
        giveaway.setPrizeRequestUserLimit(prizeRequestUserLimit);

        serializers.getGiveawaySerializer().saveGiveaway(botHome.getId(), giveaway);

        return giveaway;
    }

    @Transactional(rollbackOn = BotErrorException.class)
    public Giveaway saveGiveawayRaffleSettings(final int giveawayId, final Duration raffleDuration,
            final int winnerCount, final GiveawayCommandSettings startRaffle, final GiveawayCommandSettings enterRaffle,
            final GiveawayCommandSettings raffleStatus, final String winnerResponse, final String discordChannel)
            throws BotErrorException {

        Giveaway giveaway = getGiveaway(giveawayId);
        if (giveaway.getState() != Giveaway.State.CONFIGURING) {
            throw new BotErrorException("Can only save configuration when giveaway in in configuring state");
        }
        RaffleSettings previousSettings = giveaway.getRaffleSettings();

        RaffleSettings raffleSettings = new RaffleSettings(startRaffle, enterRaffle, raffleStatus, raffleDuration,
                winnerCount, winnerResponse, discordChannel);

        CommandTable commandTable = botHome.getCommandTable();
        raffleSettings.validateOnSave(previousSettings, commandTable);

        GiveawayEdit giveawayEdit = new GiveawayEdit();
        giveawayEdit.addGiveaway(botHome.getId(), giveaway);

        if (!startRaffle.equals(previousSettings.getStartRaffle())) {
            Command oldCommand = giveaway.getStartRaffleCommand();
            if (oldCommand != null) {
                giveawayEdit.merge(commandTable.deleteCommand(oldCommand.getId()));
            }

            StartRaffleCommand startRaffleCommand = new StartRaffleCommand(Command.UNREGISTERED_ID,
                new CommandSettings(startRaffle.getFlags(), Permission.STREAMER, new RateLimit()), giveawayId,
                startRaffle.getMessage());
            giveawayEdit.merge(commandTable.addCommand(startRaffle.getCommandName(), startRaffleCommand));
            giveaway.setStartRaffleCommand(startRaffleCommand);
        }

        giveaway.setRaffleSettings(raffleSettings);
        serializers.getGiveawaySerializer().commit(giveawayEdit);

        return giveaway;
    }

    @Transactional(rollbackOn = BotErrorException.class)
    public Giveaway startGiveaway(final int giveawayId) throws BotErrorException {
        Giveaway giveaway = getGiveaway(giveawayId);

        CommandTable commandTable = botHome.getCommandTable();
        GiveawayEdit giveawayEdit = giveaway.start(botHome.getId(), commandTable);
        serializers.getGiveawaySerializer().commit(giveawayEdit);

        return giveaway;
    }

    @Transactional(rollbackOn = BotErrorException.class)
    public Prize addPrize(final int giveawayId, final String reward, final String description)
            throws BotErrorException {
        Giveaway giveaway = getGiveaway(giveawayId);

        Prize prize = new Prize(Prize.UNREGISTERED_ID, Strings.trim(reward), Strings.trim(description));
        giveaway.addPrize(prize);

        GiveawayEdit giveawayEdit = new GiveawayEdit();
        giveawayEdit.savePrize(giveaway.getId(), prize);
        serializers.getGiveawaySerializer().commit(giveawayEdit);

        return prize;
    }

    @Transactional(rollbackOn = BotErrorException.class)
    public List<Prize> addPrizes(final int giveawayId, final String rewards, final String description)
            throws BotErrorException {
        Giveaway giveaway = getGiveaway(giveawayId);
        List<Prize> prizes = new ArrayList<>();

        GiveawayEdit giveawayEdit = new GiveawayEdit();
        for(String reward : rewards.split("\\r?\\n")) {
            reward = Strings.trim(reward);
            if (!reward.isEmpty()) {
                Prize prize = new Prize(Prize.UNREGISTERED_ID, reward, Strings.trim(description));
                prizes.add(prize);
                giveawayEdit.savePrize(giveaway.getId(), prize);
            }
        }
        prizes.forEach(giveaway::addPrize);

        serializers.getGiveawaySerializer().commit(giveawayEdit);
        return prizes;
    }

    @Transactional(rollbackOn = BotErrorException.class)
    public Prize requestPrize(final int giveawayId, final HomedUser requester) throws BotErrorException {
        Giveaway giveaway = getGiveaway(giveawayId);

        GiveawayEdit giveawayEdit = giveaway.requestPrize(requester);
        Prize prize = giveawayEdit.getSavedPrizes().keySet().iterator().next();
        serializers.getGiveawaySerializer().commit(giveawayEdit);

        return prize;
    }

    @Transactional(rollbackOn = BotErrorException.class)
    public Raffle startRaffle(final int giveawayId) throws BotErrorException {
        Giveaway giveaway = getGiveaway(giveawayId);

        RaffleSettings raffleSettings = giveaway.getRaffleSettings();
        CommandTable commandTable = botHome.getCommandTable();
        raffleSettings.validateOnStart(commandTable);

        GiveawayEdit giveawayEdit = giveaway.reservePrizes(raffleSettings.getWinnerCount());
        List<Prize> prizes = new ArrayList<>(giveawayEdit.getSavedPrizes().keySet());

        GiveawayCommandSettings enterCommandSettings = raffleSettings.getEnterRaffle();
        EnterRaffleCommand enterRaffleCommand = new EnterRaffleCommand(Command.UNREGISTERED_ID,
            new CommandSettings(enterCommandSettings.getFlags(), enterCommandSettings.getPermission(), new RateLimit()),
            giveawayId, enterCommandSettings.getMessage());
        giveawayEdit.merge(commandTable.addCommand(enterCommandSettings.getCommandName(), enterRaffleCommand));

        Duration duration = raffleSettings.getDuration();

        Map<String, Command> tokenMap = new HashMap<>();
        List<Alert> alerts = new ArrayList<>();
        List<Command> alertCommands = new ArrayList<>();
        String winnerAlertToken = "winner";
        alerts.add(new Alert(duration, winnerAlertToken));
        int flags = Command.DEFAULT_FLAGS | Command.TEMPORARY_FLAG;
        SelectWinnerCommand selectWinnerCommand = new SelectWinnerCommand(Command.UNREGISTERED_ID,
            new CommandSettings(flags, Permission.STREAMER, new RateLimit()), giveawayId,
            raffleSettings.getWinnerResponse(), raffleSettings.getDiscordChannel());
        tokenMap.put(winnerAlertToken, selectWinnerCommand);
        giveawayEdit.merge(commandTable.addCommand(selectWinnerCommand));

        RaffleStatusCommand raffleStatusCommand = null;

        if (raffleSettings.hasRaffleStatusCommand()) {
            GiveawayCommandSettings raffleStatusSettings = raffleSettings.getRaffleStatus();
            raffleStatusCommand = new RaffleStatusCommand(Command.UNREGISTERED_ID,
                new CommandSettings(raffleStatusSettings.getFlags(), raffleStatusSettings.getPermission(),
                        new RateLimit()), giveawayId, raffleStatusSettings.getMessage());
            giveawayEdit.merge(
                    commandTable.addCommand(raffleSettings.getRaffleStatus().getCommandName(), raffleStatusCommand));
        }

        int[] waitMinutes = { 5, 1 };
        int[] thresholdMinutes = { 10, 3 };
        for (int i = 0; i < waitMinutes.length; i++) {
            if (duration.toMinutes() >= thresholdMinutes[i]) {
                String waitAlertToken = String.format("min%d", waitMinutes[i]);
                Duration waitDuration = Duration.of(duration.toMinutes() - waitMinutes[i], ChronoUnit.MINUTES);
                alerts.add(new Alert(waitDuration, waitAlertToken));
                String alertMessage = String.format("%d minutes left in the raffle.", waitMinutes[i]);
                Command alertCommand = new MessageChannelCommand(Command.UNREGISTERED_ID,
                        new CommandSettings(flags, Permission.ANYONE, new RateLimit()),
                        TwitchService.TYPE, getTwitchChannelName(), alertMessage);

                tokenMap.put(waitAlertToken, alertCommand);
                alertCommands.add(alertCommand);
                giveawayEdit.merge(commandTable.addCommand(alertCommand));
            }
        }

        Instant stopTime = Instant.now().plus(duration);
        Raffle raffle = new Raffle(Raffle.UNREGISTERED_ID, enterCommandSettings.getCommandName(), enterRaffleCommand,
                raffleStatusCommand, selectWinnerCommand, alertCommands, prizes, stopTime);
        giveaway.addRaffle(raffle);

        serializers.getGiveawaySerializer().commit(giveawayEdit);
        // TODO: make it so this can go with the previous commit
        for (Map.Entry<String, Command> entry : tokenMap.entrySet()) {
            CommandTableEdit commandTableEdit =
                    commandTable.addTrigger(entry.getValue().getId(), CommandAlert.TYPE, entry.getKey());
            serializers.getCommandTableSerializer().commit(commandTableEdit);
        }

        for (Alert alert : alerts) {
            scheduleAlert(alert);
        }

        return raffle;
    }

    @Transactional(rollbackOn = BotErrorException.class)
    public void enterRaffle(final HomedUser entrant, final int giveawayId) throws BotErrorException {
        Giveaway giveaway = getGiveaway(giveawayId);

        Raffle raffle = giveaway.retrieveCurrentRaffle();
        raffle.enter(entrant);
    }


    @Transactional(rollbackOn = BotErrorException.class)
    public List<HomedUser> selectRaffleWinners(int giveawayId) throws BotErrorException {
        Giveaway giveaway = getGiveaway(giveawayId);
        Raffle raffle = giveaway.retrieveCurrentRaffle();

        GiveawayEdit giveawayEdit = new GiveawayEdit();
        CommandTable commandTable = botHome.getCommandTable();
        List<HomedUser> winners = raffle.selectWinners(giveaway, commandTable, giveawayEdit);

        for (Prize prize : raffle.getPrizes()) {
            HomedUser winner = prize.getWinner();
            if (winner != null && winner.getDiscordId() != 0) {
                String prizeMessage = String.format("Congratulations %s, your code is: %s", winner.getDiscordUsername(),
                        prize.getReward());
                whisperMessage(DiscordService.TYPE, winner, prizeMessage);
                prize.bestowTo(winner);
            }
        }
        serializers.getGiveawaySerializer().commit(giveawayEdit);

        return winners;
    }

    @Transactional(rollbackOn = BotErrorException.class)
    public boolean bestowPrize(int giveawayId, int prizeId) throws BotErrorException {
        Giveaway giveaway = getGiveaway(giveawayId);

        GiveawayEdit giveawayEdit = giveaway.bestowPrize(prizeId);
        serializers.getGiveawaySerializer().commit(giveawayEdit);
        return true;
    }

    public void deletePrize(final int giveawayId, final int prizeId) throws BotErrorException {
        GiveawayEdit giveawayEdit = botHome.getGiveaway(giveawayId).deletePrize(prizeId);
        serializers.getGiveawaySerializer().commit(giveawayEdit);
    }

    public String getTwitchChannelName() {
        return ((TwitchServiceHome) botHome.getServiceHome(TwitchService.TYPE)).getChannelName();
    }

    private void whisperMessage(final int serviceType, final HomedUser user, final String message) {
        ServiceHome serviceHome = botHome.getServiceHome(serviceType);
        serviceHome.getService().whisper(user.getUser(), message);
    }

    public List<User> getArenaUsers() throws BotErrorException {
        return serializers.getUserSerializer().getArenaUsers(botHome.getId());
    }

    private GameQueue getGameQueue(final int gameQueueId) throws BotErrorException {
        GameQueue gameQueue = botHome.getGameQueue(gameQueueId);
        if (gameQueue == null) {
            throw new BotErrorException("No Game Queue");
        }
        return gameQueue;
    }

    private String describePlayer(final int userId) throws BotErrorException {
        User user = serializers.getUserTable().getById(userId);
        if (user.getTwitchUsername() != null) {
            return user.getTwitchUsername();
        }
        return user.getDiscordUsername();
    }
}
