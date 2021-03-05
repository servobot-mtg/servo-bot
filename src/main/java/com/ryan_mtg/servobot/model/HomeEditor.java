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
import com.ryan_mtg.servobot.error.SystemError;
import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.events.AlertEvent;
import com.ryan_mtg.servobot.events.BotHomeAlertEvent;
import com.ryan_mtg.servobot.model.alerts.Alert;
import com.ryan_mtg.servobot.model.alerts.AlertGenerator;
import com.ryan_mtg.servobot.model.alerts.AlertQueue;
import com.ryan_mtg.servobot.model.editors.BookTableEditor;
import com.ryan_mtg.servobot.model.editors.ChatDraftEditor;
import com.ryan_mtg.servobot.model.editors.CommandTableEditor;
import com.ryan_mtg.servobot.model.editors.GameQueueEditor;
import com.ryan_mtg.servobot.model.editors.GiveawayEditor;
import com.ryan_mtg.servobot.model.editors.RoleTableEditor;
import com.ryan_mtg.servobot.model.editors.StorageValueEditor;
import com.ryan_mtg.servobot.model.game_queue.GameQueue;
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
import com.ryan_mtg.servobot.model.storage.StringStorageValue;
import com.ryan_mtg.servobot.twitch.model.TwitchService;
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
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class HomeEditor {
    private static Logger LOGGER = LoggerFactory.getLogger(HomeEditor.class);
    private Bot bot;
    private BotHome botHome;
    private SerializerContainer serializers;

    @Getter
    private CommandTableEditor commandTableEditor;

    @Getter
    private RoleTableEditor roleTableEditor;

    @Getter
    private GiveawayEditor giveawayEditor;

    @Getter
    private ChatDraftEditor chatDraftEditor;

    @Getter
    private BookTableEditor bookTableEditor;

    @Getter
    private GameQueueEditor gameQueueEditor;

    @Getter
    private StorageValueEditor storageValueEditor;

    private static final String REQUEST_PRIZE_DESCRIPTION = "Request prize command name";

    public HomeEditor(final Bot bot, final BotHome botHome) {
        this.bot = bot;
        this.botHome = botHome;
        this.serializers = bot.getSerializers();
        this.storageValueEditor =new StorageValueEditor(botHome.getId(), botHome.getStorageTable(),
                serializers.getStorageValueSerializer());
        this.gameQueueEditor = new GameQueueEditor(botHome.getId(), botHome.getGameQueueTable(),
                serializers.getGameQueueSerializer(), storageValueEditor);
        this.commandTableEditor = new CommandTableEditor(botHome.getBookTable(), botHome.getCommandTable(),
                serializers.getCommandSerializer(), serializers.getCommandTableSerializer(),
                gameQueueEditor);
        this.roleTableEditor = new RoleTableEditor(botHome.getRoleTable(), serializers.getRoleTableSerializer());
        this.giveawayEditor = new GiveawayEditor(botHome.getId(), botHome.getGiveaways(),
                serializers.getGiveawaySerializer(), botHome.getHomedUserTable());
        this.chatDraftEditor = new ChatDraftEditor(botHome.getId(), botHome.getChatDraftTable(),
                serializers.getChatDraftSerializer(), commandTableEditor, botHome.getHomedUserTable());
        this.bookTableEditor =
                new BookTableEditor(botHome.getId(), botHome.getBookTable(), serializers.getBookSerializer());
    }

    public String getTimeZone() {
        return botHome.getTimeZone();
    }

    public Scope getScope() {
        return botHome.getBotHomeScope();
    }

    public Service getService(final int serviceType) {
        return bot.getService(serviceType);
    }

    public ServiceHome getServiceHome(final int serviceType) {
        return botHome.getServiceHome(serviceType);
    }

    @Transactional(rollbackOn = Exception.class)
    public void start(final AlertQueue alertQueue) {
        botHome.start(this, alertQueue, true);
        save(botHomeRow -> botHomeRow.setFlags(botHome.getFlags()));
    }

    @Transactional(rollbackOn = Exception.class)
    public void stop() {
        botHome.stop(bot.getAlertQueue(), true);
        save(botHomeRow -> botHomeRow.setFlags(botHome.getFlags()));
    }

    @Transactional(rollbackOn = Exception.class)
    public void modifyBotName(final String botName) {
        botHome.setBotName(botName);

        save(botHomeRow -> botHomeRow.setBotName(botName));
    }

    @Transactional(rollbackOn = Exception.class)
    public void setTimeZone(final String timeZone) {
        botHome.setTimeZone(timeZone);
        botHome.getReactionTable().setTimeZone(timeZone);
        botHome.getCommandTable().setTimeZone(timeZone);
        botHome.getSchedule().setTimeZone(timeZone);

        bot.getAlertQueue().update(botHome);

        save(botHomeRow -> botHomeRow.setTimeZone(timeZone));
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
        Reaction reaction = new Reaction(Reaction.UNREGISTERED_ID, emote, secure, new AlwaysReact(), new ArrayList<>(),
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
        ServiceHome serviceHome = new MultiserviceHome(botHome.getServiceHomes(), this);
        AlertEvent alertEvent = new BotHomeAlertEvent(botHome, alertToken, serviceHome);
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

    @Transactional(rollbackOn = Exception.class)
    public StorageValue addStorageValue(final int type, final String name, final String value) throws UserError {
        StorageValue storageValue = null;
        switch (type) {
            case IntegerStorageValue.TYPE:
                storageValue = new IntegerStorageValue(StorageValue.UNREGISTERED_ID, StorageValue.GLOBAL_USER, name,
                        IntegerStorageValue.parseValue(value));
                break;
            case StringStorageValue.TYPE:
                storageValue = new StringStorageValue(StorageValue.UNREGISTERED_ID, StorageValue.GLOBAL_USER, name,
                        value);
                break;
            default:
                throw new SystemError("Unknown storage value type %d", type);
        }
        botHome.getStorageTable().registerValue(storageValue);
        serializers.getStorageValueSerializer().save(storageValue, botHome.getId());
        return storageValue;
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
        switch (storageValue.getType()) {
            case IntegerStorageValue.TYPE:
                IntegerStorageValue integerValue = (IntegerStorageValue) storageValue;
                integerValue.setValue(IntegerStorageValue.parseValue(value));
                break;
            case StringStorageValue.TYPE:
                StringStorageValue stringValue = (StringStorageValue) storageValue;
                stringValue.setValue(value);
                break;
            default:
                throw new UserError("%s has an unknown type %d.", storageValue.getName(), storageValue.getType());
        }
        serializers.getStorageValueSerializer().save(storageValue, botHome.getId());
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
    public void deleteStorageValue(final int storageValueId) {
        botHome.getStorageTable().removeVariable(storageValueId);
        serializers.getStorageValueRepository().deleteById(storageValueId);
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
    public Raffle startRaffle(final int giveawayId) throws BotHomeError, UserError {
        Giveaway giveaway = getGiveaway(giveawayId);

        RaffleSettings raffleSettings = giveaway.getRaffleSettings();
        CommandTable commandTable = botHome.getCommandTable();
        raffleSettings.validateOnStart(commandTable);

        /* TODO: The prizes should be reserved *after* the raffle is created, so that they can be given the raffle to
            save. But currently we don't serialize raffles. */
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

        //TODO: fix after raffle serialization is complete.
        raffle.setId(Raffle.FAKE_ID);
        prizes.forEach(prize -> prize.setRaffleId(raffle.getId()));

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
                // TODO: turned off because of a raffle with no code, this should be configurable
                String prizeMessage = String.format("Congratulations %s, your code is: %s", winner.getDiscordUsername(),
                        prize.getReward());
                whisperMessage(DiscordService.TYPE, winner, prizeMessage);
                prize.bestowTo(winner);
            }
        }
        serializers.getGiveawaySerializer().commit(giveawayEdit);

        return winners;
    }

    public Map<String, Emote> getEmoteMap(final int serviceType) {
        return botHome.getEmoteMap(serviceType);
    }

    @Transactional(rollbackOn = Exception.class)
    public EmoteLink addEmoteLink(final String twitchEmote, final String discordEmote) throws UserError {
        EmoteLink emoteLink = new EmoteLink(EmoteLink.UNREGISTERED_ID, twitchEmote, discordEmote);
        botHome.addEmoteLink(emoteLink);
        serializers.getEmoteLinkSerializer().save(botHome.getId(), emoteLink);
        return emoteLink;
    }

    @Transactional(rollbackOn = Exception.class)
    public void deleteEmoteLink(final int emoteLinkId) {
        List<EmoteLink> emoteLinks = botHome.getEmoteLinks();
        EmoteLink emoteLink =
                emoteLinks.stream().filter(el -> el.getId() == emoteLinkId).findFirst().orElse(null);

        if (emoteLink == null) {
            return;
        }

        emoteLinks.remove(emoteLink);
        serializers.getEmoteLinkSerializer().delete(emoteLink);
    }

    public String getTwitchChannelName() {
        return botHome.getServiceHome(TwitchService.TYPE).getName();
    }

    private void whisperMessage(final int serviceType, final HomedUser user, final String message) {
        Service service = botHome.getServiceHome(serviceType).getService();
        service.whisper(user.getUser(), message);
    }

    public List<User> getArenaUsers() {
        return serializers.getUserSerializer().getArenaUsers(botHome.getId());
    }

    private GameQueue getGameQueue(final int gameQueueId) throws UserError {
        GameQueue gameQueue = botHome.getGameQueueTable().getGameQueue(gameQueueId);
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

    @Transactional(rollbackOn = Exception.class)
    private void save(final Consumer<BotHomeRow> homeModifier) {
        BotHomeRepository botHomeRepository = serializers.getBotHomeRepository();
        BotHomeRow botHomeRow = botHomeRepository.findById(botHome.getId());
        homeModifier.accept(botHomeRow);
        botHomeRepository.save(botHomeRow);
    }
}
