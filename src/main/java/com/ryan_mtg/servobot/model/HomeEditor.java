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
import com.ryan_mtg.servobot.data.factories.SerializerContainer;
import com.ryan_mtg.servobot.data.models.AlertGeneratorRow;
import com.ryan_mtg.servobot.data.models.BotHomeRow;
import com.ryan_mtg.servobot.data.models.SuggestionRow;
import com.ryan_mtg.servobot.data.repositories.BotHomeRepository;
import com.ryan_mtg.servobot.data.repositories.SuggestionRepository;
import com.ryan_mtg.servobot.discord.model.DiscordService;
import com.ryan_mtg.servobot.error.BotHomeError;
import com.ryan_mtg.servobot.error.LibraryError;
import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.events.AlertEvent;
import com.ryan_mtg.servobot.events.BotHomeAlertEvent;
import com.ryan_mtg.servobot.model.alerts.Alert;
import com.ryan_mtg.servobot.model.alerts.AlertGenerator;
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

    @Transactional(rollbackOn = Exception.class)
    public void modifyBotName(final String botName) {
        botHome.setBotName(botName);

        BotHomeRepository botHomeRepository = serializers.getBotHomeRepository();
        BotHomeRow botHomeRow = botHomeRepository.findById(botHome.getId());
        botHomeRow.setBotName(botName);
        botHomeRepository.save(botHomeRow);
    }

    @Transactional(rollbackOn = Exception.class)
    public void setTimeZone(final String timeZone) {
        botHome.setTimeZone(timeZone);
        botHome.getReactionTable().setTimeZone(timeZone);
        botHome.getCommandTable().setTimeZone(timeZone);
        botHome.getSchedule().setTimeZone(timeZone);

        bot.getAlertQueue().update(botHome);

        BotHomeRepository botHomeRepository = serializers.getBotHomeRepository();
        BotHomeRow botHomeRow = botHomeRepository.findById(botHome.getId());
        botHomeRow.setTimeZone(timeZone);
        botHomeRepository.save(botHomeRow);
    }

    public HomedUser getUserById(final int userId) {
        return botHome.getHomedUserTable().getById(userId);
    }

    public HomedUser getUserByDiscordId(final long discordId, final String discordUsername) {
        return botHome.getHomedUserTable().getByDiscordId(discordId, discordUsername);
    }

    public HomedUser getUserByTwitchId(final int twitchId, final String twitchUsername) {
        return botHome.getHomedUserTable().getByTwitchId(twitchId, twitchUsername);
    }

    @Transactional(rollbackOn = Exception.class)
    public Reaction addReaction(final String emote, final boolean secure) throws UserError {
        Reaction  reaction = new Reaction(Reaction.UNREGISTERED_ID, emote, secure, new AlwaysReact(), new ArrayList<>(),
                new ArrayList<>());
        ReactionTable reactionTable = botHome.getReactionTable();
        ReactionTableEdit reactionTableEdit = reactionTable.addReaction(reaction);
        serializers.getReactionTableSerializer().commit(botHome.getId(), reactionTableEdit);
        return reaction;
    }

    @Transactional(rollbackOn = Exception.class)
    public boolean secureReaction(final int reactionId, final boolean secure) {
        Reaction reaction = botHome.getReactionTable().secureReaction(reactionId, secure);
        serializers.getReactionSerializer().saveReaction(botHome.getId(), reaction);
        return reaction.isSecure();
    }

    @Transactional(rollbackOn = Exception.class)
    public void deleteReaction(final int reactionId) {
        ReactionTableEdit reactionTableEdit = botHome.getReactionTable().deleteReaction(reactionId);
        serializers.getReactionTableSerializer().commit(botHome.getId(), reactionTableEdit);
    }

    @Transactional(rollbackOn = Exception.class)
    public Pattern addPattern(final int reactionId, final String pattern) throws UserError {
        ReactionTableEdit reactionTableEdit = botHome.getReactionTable().addPattern(reactionId, pattern);
        serializers.getReactionTableSerializer().commit(botHome.getId(), reactionTableEdit);
        return reactionTableEdit.getSavedPatterns().keySet().iterator().next();
    }

    @Transactional(rollbackOn = Exception.class)
    public void deletePattern(final int reactionId, final int patternId) {
        ReactionTableEdit reactionTableEdit = botHome.getReactionTable().deletePattern(reactionId, patternId);
        serializers.getReactionTableSerializer().commit(botHome.getId(), reactionTableEdit);
    }

    public void scheduleAlert(final Alert alert) {
        bot.getAlertQueue().scheduleAlert(botHome, alert);
    }

    public void alert(final String alertToken) {
        Home home = new MultiServiceHome(botHome.getServiceHomes(), this);
        AlertEvent alertEvent = new BotHomeAlertEvent(botHome, alertToken, home);
        botHome.getEventListener().onAlert(alertEvent);
    }

    @Transactional(rollbackOn = Exception.class)
    public AlertGenerator addAlert(final int type, final String keyword, final int time) throws UserError {
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

    @Transactional(rollbackOn = Exception.class)
    public void deleteAlert(final int alertGeneratorId) {
        CommandTableEdit commandTableEdit = botHome.getCommandTable().deleteAlertGenerator(alertGeneratorId);
        serializers.getCommandTableSerializer().commit(commandTableEdit);
    }

    @Transactional(rollbackOn = Exception.class)
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

    public StorageValue getStorageValue(final String name) throws UserError {
        StorageValue.validateName(name);
        StorageValue storageValue = botHome.getStorageTable().getStorage(name);
        if (storageValue == null) {
            throw new UserError("No value with name %s.", name);
        }
        return storageValue;
    }

    public List<StorageValue> getAllUsersStorageValues(final String name) throws UserError {
        StorageValue.validateName(name);
        return botHome.getStorageTable().getAllUsersStorage(name);
    }

    public IntegerStorageValue getStorageValue(final int userId, final String name, final int defaultValue)
            throws UserError {
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

        throw new UserError("%s is not a number", name);
    }

    @Transactional(rollbackOn = Exception.class)
    public StorageValue setStorageValue(final String name, final String value) throws UserError {
        StorageValue storageValue = getStorageValue(name);
        if (storageValue instanceof IntegerStorageValue) {
            IntegerStorageValue integerValue = (IntegerStorageValue) storageValue;
            try {
                integerValue.setValue(Integer.parseInt(value));
            } catch (Exception e) {
                throw new UserError("Invalid value %s.", value);
            }
            serializers.getStorageValueSerializer().save(integerValue, botHome.getId());
        } else {
            throw new UserError("%s has an unknown type of value.", storageValue.getName());
        }
        return storageValue;
    }

    @Transactional(rollbackOn = Exception.class)
    public void removeStorageVariables(final String name) {
        botHome.getStorageTable().removeVariables(name);
        serializers.getStorageTableSerializer().removeVariables(name, botHome.getId());
    }

    @Transactional(rollbackOn = Exception.class)
    public void removeStorageVariable(final int userId, final String name) {
        StorageValue storageValue = botHome.getStorageTable().removeVariable(userId, name);
        if (storageValue != null) {
            serializers.getStorageValueRepository().deleteById(storageValue.getId());
        }
    }

    @Transactional(rollbackOn = Exception.class)
    public IntegerStorageValue incrementStorageValue(final int userId, final String name, final int defaultValue)
            throws UserError {
        return increaseStorageValue(userId, name, 1, defaultValue);
    }

    @Transactional(rollbackOn = Exception.class)
    public IntegerStorageValue increaseStorageValue(final int userId, final String name, final int amount,
            final int defaultValue) throws UserError {
        IntegerStorageValue value = getStorageValue(userId, name, defaultValue);
        value.setValue(value.getValue() + amount);
        serializers.getStorageValueSerializer().save(value, botHome.getId());
        return value;
    }

    @Transactional(rollbackOn = Exception.class)
    public String startGameQueue(final int gameQueueId, final String name) throws UserError {
        GameQueue gameQueue = getGameQueue(gameQueueId);

        if (gameQueue.getState() == GameQueue.State.PLAYING) {
            if (name != null) {
                setGameQueueName(gameQueueId, name);
                return String.format("Game queue name changed to '%s.'", name);
            }
            throw new  UserError("Game queue '%s' already started.", gameQueue.getName());
        }

        gameQueue.setState(GameQueue.State.PLAYING);
        if (name != null) {
            gameQueue.setName(name);
        }

        serializers.getGameQueueSerializer().saveGameQueue(gameQueue);
        return String.format("Game queue '%s' started.", gameQueue.getName());
    }

    @Transactional(rollbackOn = Exception.class)
    public String closeGameQueue(final int gameQueueId) throws UserError {
        GameQueue gameQueue = getGameQueue(gameQueueId);

        if (gameQueue.getState() == GameQueue.State.CLOSED) {
            throw new UserError("Game queue '%s' is already closed.", gameQueue.getName());
        }

        if (gameQueue.getState() != GameQueue.State.PLAYING) {
            throw new UserError("Game queue '%s' is not open.", gameQueue.getName());
        }

        gameQueue.setState(GameQueue.State.CLOSED);

        serializers.getGameQueueSerializer().saveGameQueue(gameQueue);
        return String.format("Queue '%s' is now closed.", gameQueue.getName());
    }

    @Transactional(rollbackOn = Exception.class)
    public User popGameQueue(final int gameQueueId) throws UserError {
        GameQueue gameQueue = getGameQueue(gameQueueId);
        if (gameQueue.getState() == GameQueue.State.IDLE) {
            throw new UserError("Game queue '%s' is not active.", gameQueue.getName());
        }

        int nextPlayer = gameQueue.pop();
        if (nextPlayer == EMPTY_QUEUE) {
            throw new UserError("No players in the queue.");
        }

        serializers.getGameQueueSerializer().removeEntry(gameQueue, nextPlayer);
        return serializers.getUserTable().getById(gameQueue.getCurrentPlayerId());
    }

    public User peekGameQueue(final int gameQueueId) throws UserError {
        GameQueue gameQueue = getGameQueue(gameQueueId);
        if (gameQueue.getState() == GameQueue.State.IDLE) {
            throw new UserError("Game queue '%s' is not active.", gameQueue.getName());
        }

        int nextPlayer = gameQueue.getCurrentPlayerId();
        if (nextPlayer == EMPTY_QUEUE) {
            throw new UserError("No players in the queue.");
        }

        return serializers.getUserTable().getById(gameQueue.getCurrentPlayerId());
    }

    @Transactional(rollbackOn = Exception.class)
    public int joinGameQueue(final int gameQueueId, final com.ryan_mtg.servobot.model.User player) throws UserError {
        GameQueue gameQueue = getGameQueue(gameQueueId);

        int playerId = player.getId();

        if (gameQueue.contains(playerId)) {
            throw new UserError("%s is already in the queue.", player.getName());
        }

        if (gameQueue.getState() == GameQueue.State.IDLE) {
            throw new UserError("Game queue '%s' is not active.", gameQueue.getName());
        }
        if (gameQueue.getState() == GameQueue.State.CLOSED) {
            throw new UserError("Game queue '%s' is not allowing new players.", gameQueue.getName());
        }

        GameQueueEntry gameQueueEntry = gameQueue.enqueue(playerId);

        serializers.getGameQueueSerializer().addEntry(gameQueue, gameQueueEntry);
        return gameQueueEntry.getPosition();
    }

    @Transactional(rollbackOn = Exception.class)
    public void removeFromGameQueue(final int gameQueueId, final com.ryan_mtg.servobot.model.User player)
            throws UserError {
        GameQueue gameQueue = getGameQueue(gameQueueId);

        int playerId = player.getId();

        if (gameQueue.getState() == GameQueue.State.IDLE) {
            throw new UserError("Game queue '%s' is not active.", gameQueue.getName());
        }

        if (gameQueue.getCurrentPlayerId() == playerId) {
            throw new UserError("%s is currently playing.", player.getName());
        }

        if (!gameQueue.contains(playerId)) {
            throw new UserError("%s is not in the queue.", player.getName());
        }

        gameQueue.remove(playerId);
        serializers.getGameQueueSerializer().removeEntry(gameQueue, playerId);
    }

    @Transactional(rollbackOn = Exception.class)
    public void setGameQueueName(final int gameQueueId, final String name) throws UserError {
        GameQueue gameQueue = getGameQueue(gameQueueId);

        if (name.equals(gameQueue.getName())) {
            return;
        }

        gameQueue.setName(name);

        serializers.getGameQueueSerializer().saveGameQueue(gameQueue);
    }

    @Transactional(rollbackOn = Exception.class)
    public String stopGameQueue(int gameQueueId) throws UserError {
        GameQueue gameQueue = getGameQueue(gameQueueId);
        if (gameQueue.getState() == GameQueue.State.IDLE) {
            throw new UserError("Game queue '%s' has already been stopped.", gameQueue.getName());
        }

        gameQueue.setState(GameQueue.State.IDLE);

        serializers.getGameQueueSerializer().emptyGameQueue(gameQueue);
        return String.format("Game queue '%s' stopped.", gameQueue.getName());
    }

    @Transactional(rollbackOn = Exception.class)
    public String showGameQueue(final int gameQueueId) throws UserError {
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

    @Transactional(rollbackOn = Exception.class)
    public Giveaway addGiveaway(final String name, final boolean selfService, final boolean raffle) throws UserError {
        Giveaway giveaway = new Giveaway(Giveaway.UNREGISTERED_ID, name, selfService, raffle);
        serializers.getGiveawaySerializer().saveGiveaway(botHome.getId(), giveaway);
        botHome.addGiveaway(giveaway);
        return giveaway;
    }

    @Transactional(rollbackOn = Exception.class)
    public Giveaway saveGiveawaySelfService(final int giveawayId, final String requestPrizeCommandName,
            final int prizeRequestLimit, final int prizeRequestUserLimit) throws UserError {
        Giveaway giveaway = getGiveaway(giveawayId);
        if (giveaway.getState() != Giveaway.State.CONFIGURING) {
            throw new UserError("Can only save configuration when giveaway in in configuring state");
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

    @Transactional(rollbackOn = Exception.class)
    public Giveaway saveGiveawayRaffleSettings(final int giveawayId, final Duration raffleDuration,
            final int winnerCount, final GiveawayCommandSettings startRaffle, final GiveawayCommandSettings enterRaffle,
            final GiveawayCommandSettings raffleStatus, final GiveawayCommandSettings selectWinner,
            final String discordChannel, final boolean timed) throws UserError {

        Giveaway giveaway = getGiveaway(giveawayId);
        if (giveaway.getState() != Giveaway.State.CONFIGURING) {
            throw new UserError("Can only save configuration when giveaway in in configuring state");
        }
        RaffleSettings previousSettings = giveaway.getRaffleSettings();

        RaffleSettings raffleSettings = new RaffleSettings(timed, startRaffle, enterRaffle, raffleStatus, raffleDuration,
                winnerCount, selectWinner, discordChannel);

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

    @Transactional(rollbackOn = Exception.class)
    public Giveaway startGiveaway(final int giveawayId) throws UserError {
        Giveaway giveaway = getGiveaway(giveawayId);

        CommandTable commandTable = botHome.getCommandTable();
        GiveawayEdit giveawayEdit = giveaway.start(botHome.getId(), commandTable);
        serializers.getGiveawaySerializer().commit(giveawayEdit);

        return giveaway;
    }

    @Transactional(rollbackOn = Exception.class)
    public Prize addPrize(final int giveawayId, final String reward, final String description) throws UserError {
        Giveaway giveaway = getGiveaway(giveawayId);

        Prize prize = new Prize(Prize.UNREGISTERED_ID, Strings.trim(reward), Strings.trim(description));
        giveaway.addPrize(prize);

        GiveawayEdit giveawayEdit = new GiveawayEdit();
        giveawayEdit.savePrize(giveaway.getId(), prize);
        serializers.getGiveawaySerializer().commit(giveawayEdit);

        return prize;
    }

    @Transactional(rollbackOn = Exception.class)
    public List<Prize> addPrizes(final int giveawayId, final String rewards, final String description)
            throws UserError {
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

    @Transactional(rollbackOn = Exception.class)
    public Prize requestPrize(final int giveawayId, final HomedUser requester) throws BotHomeError, UserError {
        Giveaway giveaway = getGiveaway(giveawayId);

        GiveawayEdit giveawayEdit = giveaway.requestPrize(requester);
        Prize prize = giveawayEdit.getSavedPrizes().keySet().iterator().next();
        serializers.getGiveawaySerializer().commit(giveawayEdit);

        return prize;
    }

    @Transactional(rollbackOn = Exception.class)
    public Raffle startRaffle(final int giveawayId) throws BotHomeError, UserError {
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

        List<Alert> alerts = new ArrayList<>();
        List<Command> alertCommands = new ArrayList<>();

        GiveawayCommandSettings selectWinnerCommandSettings = raffleSettings.getSelectWinner();
        SelectWinnerCommand selectWinnerCommand = new SelectWinnerCommand(Command.UNREGISTERED_ID,
            new CommandSettings(selectWinnerCommandSettings.getFlags(), selectWinnerCommandSettings.getPermission(),
            new RateLimit()), giveawayId, selectWinnerCommandSettings.getMessage(), raffleSettings.getDiscordChannel());

        if (!Strings.isBlank(selectWinnerCommandSettings.getCommandName())) {
            giveawayEdit.merge(
                    commandTable.addCommand(selectWinnerCommandSettings.getCommandName(), selectWinnerCommand));
        } else {
            giveawayEdit.merge(commandTable.addCommand(selectWinnerCommand));
        }

        if (raffleSettings.isTimed()) {
            String winnerAlertToken = "winner";
            alerts.add(new Alert(duration, winnerAlertToken));
            giveawayEdit.merge(commandTable.addTrigger(selectWinnerCommand, CommandAlert.TYPE, winnerAlertToken));
        }


        RaffleStatusCommand raffleStatusCommand = null;

        if (raffleSettings.hasRaffleStatusCommand()) {
            GiveawayCommandSettings raffleStatusSettings = raffleSettings.getRaffleStatus();
            raffleStatusCommand = new RaffleStatusCommand(Command.UNREGISTERED_ID,
                new CommandSettings(raffleStatusSettings.getFlags(), raffleStatusSettings.getPermission(),
                        new RateLimit()), giveawayId, raffleStatusSettings.getMessage());
            giveawayEdit.merge(
                    commandTable.addCommand(raffleSettings.getRaffleStatus().getCommandName(), raffleStatusCommand));
        }

        if (raffleSettings.isTimed()) {
            int[] waitMinutes = {5, 1};
            int[] thresholdMinutes = {10, 3};
            for (int i = 0; i < waitMinutes.length; i++) {
                if (duration.toMinutes() >= thresholdMinutes[i]) {
                    String waitAlertToken = String.format("min%d", waitMinutes[i]);
                    Duration waitDuration = Duration.of(duration.toMinutes() - waitMinutes[i], ChronoUnit.MINUTES);
                    alerts.add(new Alert(waitDuration, waitAlertToken));
                    String alertMessage = String.format("%d minutes left in the raffle.", waitMinutes[i]);
                    int flags = Command.DEFAULT_FLAGS | Command.TEMPORARY_FLAG;
                    Command alertCommand = new MessageChannelCommand(Command.UNREGISTERED_ID,
                            new CommandSettings(flags, Permission.ANYONE, new RateLimit()),
                            TwitchService.TYPE, getTwitchChannelName(), alertMessage);

                    alertCommands.add(alertCommand);
                    giveawayEdit.merge(commandTable.addCommand(alertCommand));
                    giveawayEdit.merge(commandTable.addTrigger(alertCommand, CommandAlert.TYPE, waitAlertToken));
                }
            }
        }

        Instant stopTime = raffleSettings.isTimed() ? Instant.now().plus(duration) : null;
        Raffle raffle = new Raffle(Raffle.UNREGISTERED_ID, enterCommandSettings.getCommandName(), enterRaffleCommand,
                raffleStatusCommand, selectWinnerCommand, alertCommands, prizes, stopTime);
        giveaway.addRaffle(raffle);

        serializers.getGiveawaySerializer().commit(giveawayEdit);

        for (Alert alert : alerts) {
            scheduleAlert(alert);
        }

        return raffle;
    }

    @Transactional(rollbackOn = Exception.class)
    public Raffle enterRaffle(final HomedUser entrant, final int giveawayId) throws UserError {
        Giveaway giveaway = getGiveaway(giveawayId);

        Raffle raffle = giveaway.retrieveCurrentRaffle();
        raffle.enter(entrant);
        return raffle;
    }

    @Transactional(rollbackOn = Exception.class)
    public List<HomedUser> selectRaffleWinners(int giveawayId) throws UserError {
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

    @Transactional(rollbackOn = Exception.class)
    public boolean bestowPrize(int giveawayId, int prizeId) throws LibraryError {
        Giveaway giveaway = getGiveaway(giveawayId);

        GiveawayEdit giveawayEdit = giveaway.bestowPrize(prizeId);
        serializers.getGiveawaySerializer().commit(giveawayEdit);
        return true;
    }

    public void deletePrize(final int giveawayId, final int prizeId) throws LibraryError {
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

    public List<User> getArenaUsers() {
        return serializers.getUserSerializer().getArenaUsers(botHome.getId());
    }

    private GameQueue getGameQueue(final int gameQueueId) throws UserError {
        GameQueue gameQueue = botHome.getGameQueue(gameQueueId);
        if (gameQueue == null) {
            throw new UserError("No Game Queue");
        }
        return gameQueue;
    }

    private String describePlayer(final int userId) {
        User user = serializers.getUserTable().getById(userId);
        if (user.getTwitchUsername() != null) {
            return user.getTwitchUsername();
        }
        return user.getDiscordUsername();
    }
}
