package com.ryan_mtg.servobot.model;

import com.ryan_mtg.servobot.commands.Command;
import com.ryan_mtg.servobot.commands.CommandTable;
import com.ryan_mtg.servobot.commands.CommandTableEdit;
import com.ryan_mtg.servobot.commands.MessageCommand;
import com.ryan_mtg.servobot.commands.Permission;
import com.ryan_mtg.servobot.commands.Trigger;
import com.ryan_mtg.servobot.controllers.CommandDescriptor;
import com.ryan_mtg.servobot.data.factories.BookSerializer;
import com.ryan_mtg.servobot.data.factories.SerializerContainer;
import com.ryan_mtg.servobot.data.models.BotHomeRow;
import com.ryan_mtg.servobot.data.models.CommandRow;
import com.ryan_mtg.servobot.data.models.SuggestionRow;
import com.ryan_mtg.servobot.data.repositories.BotHomeRepository;
import com.ryan_mtg.servobot.data.repositories.SuggestionRepository;
import com.ryan_mtg.servobot.discord.model.DiscordService;
import com.ryan_mtg.servobot.events.AlertEvent;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.BotHomeAlertEvent;
import com.ryan_mtg.servobot.model.reaction.Pattern;
import com.ryan_mtg.servobot.model.reaction.Reaction;
import com.ryan_mtg.servobot.model.reaction.ReactionTableEdit;
import com.ryan_mtg.servobot.model.scope.Scope;
import com.ryan_mtg.servobot.model.storage.IntegerStorageValue;
import com.ryan_mtg.servobot.model.storage.StorageTable;
import com.ryan_mtg.servobot.model.storage.StorageValue;
import com.ryan_mtg.servobot.twitch.model.TwitchService;
import com.ryan_mtg.servobot.user.HomedUser;
import com.ryan_mtg.servobot.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.transaction.Transactional;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ryan_mtg.servobot.model.GameQueue.EMPTY_QUEUE;

public class HomeEditor {
    private static Logger LOGGER = LoggerFactory.getLogger(HomeEditor.class);
    private Bot bot;
    private BotHome botHome;
    private SerializerContainer serializers;

    public HomeEditor(final Bot bot, final BotHome botHome) {
        this.bot = bot;
        this.botHome = botHome;
        this.serializers = bot.getSerializers();
    }

    public Scope getScope() {
        return botHome.getBotHomeScope();
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

    @Transactional(rollbackOn = BotErrorException.class)
    public boolean secureCommand(final int commandId, final boolean secure) {
        Command command = botHome.getCommandTable().secureCommand(commandId, secure);
        serializers.getCommandSerializer().saveCommand(botHome.getId(), command);
        return command.isSecure();
    }

    @Transactional(rollbackOn = BotErrorException.class)
    public Reaction addReaction(final String emote, final boolean secure) throws BotErrorException {
        //return new Reaction(Reaction.UNREGISTERED_ID, emote, secure);
        throw new BotErrorException("Not supported");
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
    public CommandDescriptor addCommand(final CommandRow commandRow) throws BotErrorException {
        Map<Integer, Book> bookMap = new HashMap<>();
        for (Book book : botHome.getBooks()) {
            bookMap.put(book.getId(), book);
        }

        Command command = serializers.getCommandSerializer().createCommand(commandRow, bookMap);

        CommandTable commandTable = botHome.getCommandTable();
        CommandTableEdit commandTableEdit = commandTable.addCommand(command);
        serializers.getCommandTableSerializer().commit(botHome.getId(), commandTableEdit);

        return new CommandDescriptor(command);
    }

    @Transactional(rollbackOn = BotErrorException.class)
    public void addCommand(final String alias, final MessageCommand command) throws BotErrorException {
        CommandTable commandTable = botHome.getCommandTable();

        CommandTableEdit commandTableEdit = commandTable.addCommand(alias, command);
        serializers.getCommandTableSerializer().commit(botHome.getId(), commandTableEdit);
    }

    @Transactional(rollbackOn = BotErrorException.class)
    public void deleteCommand(final com.ryan_mtg.servobot.model.User deleter, final String commandName)
            throws BotErrorException {
        CommandTable commandTable = botHome.getCommandTable();
        Command command = commandTable.getCommand(commandName);

        if (command == null) {
            throw new BotErrorException(String.format("No command named '%s.'", commandName));
        }

        if (!command.hasPermissions(deleter)) {
            throw new BotErrorException(
                    String.format("%s is not allowed to delete '%s.'", deleter.getName(), commandName));
        }

        CommandTableEdit commandTableEdit = commandTable.deleteCommand(commandName);

        serializers.getCommandTableSerializer().commit(botHome.getId(), commandTableEdit);
    }

    @Transactional(rollbackOn = BotErrorException.class)
    public void deleteCommand(final int commandId) throws BotErrorException {
        CommandTable commandTable = botHome.getCommandTable();
        CommandTableEdit commandTableEdit = commandTable.deleteCommand(commandId);

        if (commandTableEdit.getDeletedCommands().isEmpty()) {
            throw new BotErrorException(String.format("Command '%d' not found.", commandId));
        }
        serializers.getCommandTableSerializer().commit(botHome.getId(), commandTableEdit);
    }

    @Transactional(rollbackOn = BotErrorException.class)
    public List<Trigger> addTrigger(final int commandId, final int triggerType, final String text) throws BotErrorException {
        CommandTable commandTable = botHome.getCommandTable();
        CommandTableEdit commandTableEdit = commandTable.addTrigger(commandId, triggerType, text);

        if (commandTableEdit.getSavedTriggers().size() != 1) {
            throw new BotErrorException(String.format("Trigger '%s' not added.", text));
        }

        serializers.getCommandTableSerializer().commit(botHome.getId(), commandTableEdit);
        Trigger trigger = commandTableEdit.getSavedTriggers().keySet().iterator().next();
        List<Trigger> response = new ArrayList<>();
        response.add(trigger);
        if (!commandTableEdit.getDeletedTriggers().isEmpty()) {
            Trigger deletedTrigger = commandTableEdit.getDeletedTriggers().get(0);
            response.add(deletedTrigger);
        }
        return response;
    }

    @Transactional(rollbackOn = BotErrorException.class)
    public void deleteTrigger(final int triggerId) throws BotErrorException {
        Trigger trigger = serializers.getCommandSerializer().getTrigger(triggerId);
        CommandTable commandTable = botHome.getCommandTable();
        CommandTableEdit commandTableEdit = commandTable.deleteTrigger(trigger);

        if (commandTableEdit.getDeletedTriggers().isEmpty()) {
            throw new BotErrorException(String.format("Trigger '%d' not found.", trigger.getId()));
        }
        serializers.getCommandTableSerializer().commit(botHome.getId(), commandTableEdit);
    }

    public Statement addStatement(final int bookId, final String text) throws BotErrorException {
        return addStatement(getBook(bookId), text);
    }

    public void addStatement(final String bookName, final String text) throws BotErrorException {
        addStatement(getBook(bookName), text);
    }

    @Transactional(rollbackOn = BotErrorException.class)
    public void deleteStatement(final int bookId, final int statementId) {
        botHome.getBooks().stream().filter(book -> book.getId() == bookId).forEach(book -> {
            book.deleteStatement(statementId);
            serializers.getStatementRepository().deleteById(statementId);
        });
    }

    @Transactional(rollbackOn = BotErrorException.class)
    public void modifyStatement(final int bookId, final int statementId, final String text) {
        BookSerializer bookSerializer = serializers.getBookSerializer();
        botHome.getBooks().stream().filter(book -> book.getId() == bookId).forEach(book -> {
            book.getStatements().stream().filter(statement -> statement.getId() ==statementId).forEach(statement -> {
                statement.setText(text);
                bookSerializer.saveStatement(bookId, statement);
            });
        });
    }

    public void scheduleAlert(final Duration delay, final String alertToken) {
        bot.getAlertQueue().scheduleAlert(botHome, delay, alertToken);
    }

    public void alert(final String alertToken) {
        Home home = new MultiServiceHome(botHome.getServiceHomes(), this);
        AlertEvent alertEvent = new BotHomeAlertEvent(botHome.getId(), alertToken, home);
        botHome.getListener().onAlert(alertEvent);
    }

    @Transactional(rollbackOn = BotErrorException.class)
    public boolean setCommandService(final int commandId, final int serivceType, final boolean value) {
        Command command = botHome.getCommandTable().getCommand(commandId);
        command.setService(serivceType, value);
        serializers.getCommandSerializer().saveCommand(botHome.getId(), command);
        return command.getService(serivceType);
    }

    @Transactional(rollbackOn = BotErrorException.class)
    public Permission setCommandPermission(final int commandId, final Permission permission) {
        Command command = botHome.getCommandTable().getCommand(commandId);
        command.setPermission(permission);
        serializers.getCommandSerializer().saveCommand(botHome.getId(), command);
        return command.getPermission();
    }

    @Transactional(rollbackOn = BotErrorException.class)
    public void addSuggestion(final String command) {
        String alias = command.toLowerCase();
        if (alias.length() > SuggestionRow.MAX_SUGGESTION_SIZE) {
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
    public void remoteStorageVariables(final String name) {
        botHome.getStorageTable().removeVariables(name);
        serializers.getStorageTableSerializer().removeVariables(name, botHome.getId());
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
    public IntegerStorageValue incrementStorageValue(final int userId, final String name, final int defaultVariable)
            throws BotErrorException {
        IntegerStorageValue value = getStorageValue(userId, name, defaultVariable);
        value.setValue(value.getValue() + 1);
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
        return serializers.getUserSerializer().lookupById(gameQueue.getCurrentPlayerId());
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

        return serializers.getUserSerializer().lookupById(gameQueue.getCurrentPlayerId());
    }

    @Transactional(rollbackOn = BotErrorException.class)
    public int joinGameQueue(final int gameQueueId, final com.ryan_mtg.servobot.model.User player)
            throws BotErrorException {
        GameQueue gameQueue = getGameQueue(gameQueueId);

        int playerId = player.getHomedUser().getId();

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

        int playerId = player.getHomedUser().getId();

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

    public Reward startGiveaway() throws BotErrorException {
        Reward reward = botHome.getGiveaway().startGiveaway();
        if (Giveaway.DURATION.toMinutes() > 5) {
            scheduleAlert(Giveaway.DURATION.minus(5, ChronoUnit.MINUTES), "5min");
        }

        if (Giveaway.DURATION.toMinutes() > 1) {
            scheduleAlert(Giveaway.DURATION.minus(1, ChronoUnit.MINUTES), "1min");
        }

        scheduleAlert(Giveaway.DURATION, "winner");
        LOGGER.info("scheduled: " + reward.getPrize());
        return reward;
    }

    public void enterGiveaway(final HomedUser homedUser) throws BotErrorException {
        botHome.getGiveaway().enterGiveaway(homedUser);
    }

    public Reward getGiveaway() {
        return botHome.getGiveaway().getCurrentReward();
    }

    public Reward addReward(final String prize) {
        Reward reward = new Reward(Giveaway.nextRewardId, prize);
        Giveaway.nextRewardId++;
        botHome.getGiveaway().addReward(reward);
        return reward;
    }

    public HomedUser awardReward(final int rewardId) throws BotErrorException {
        Reward reward = getReward(rewardId);

        if (reward.getStatus() == Reward.Status.IN_PROGRESS && reward.getTimeLeft().isZero()) {
            reward.setStatus(Reward.Status.CONCLUDED);
        }

        reward.award();

        String message = String.format("The giveaway winner is " + reward.getWinner().getName());
        sendMessage(DiscordService.TYPE, Giveaway.DISCORD_CHANNEL, message);
        sendMessage(TwitchService.TYPE, Giveaway.TWITCH_CHANNEL, message);
        return reward.getWinner();
    }

    public boolean bestowReward(final int rewardId) throws BotErrorException {
        Reward reward = getReward(rewardId);
        if (reward.getStatus() != Reward.Status.AWARDED) {
            throw new BotErrorException("Invalid reward state");
        }
        String prize = reward.getPrize();
        HomedUser winner = reward.getWinner();
        botHome.getGiveaway().finishGiveaway();

        String announcement = "Congratulations, your code is: " + prize;
        bot.getService(DiscordService.TYPE).whisper(winner, announcement);
        return true;
    }

    private void sendMessage(final int serviceType, final String channelName, final String message) {
        botHome.getServiceHome(serviceType).getHome().getChannel(channelName, serviceType).say(message);
    }

    public void deleteReward(final int rewardId) {
        botHome.getGiveaway().deleteReward(rewardId);
    }

    public List<User> getArenaUsers() throws BotErrorException {
        return serializers.getUserSerializer().getArenaUsers();
    }

    private GameQueue getGameQueue(final int gameQueueId) throws BotErrorException {
        GameQueue gameQueue = botHome.getGameQueue(gameQueueId);
        if (gameQueue == null) {
            throw new BotErrorException("No Game Queue");
        }
        return gameQueue;
    }

    private Book getBook(final String bookName) throws BotErrorException {
        Book book = botHome.getBooks().stream()
                .filter(b -> b.getName().equalsIgnoreCase(bookName)).findFirst().orElse(null);

        if (book == null) {
            throw new BotErrorException(String.format("No book with name %s.", bookName));
        }

        return book;
    }

    private Book getBook(final int bookId) {
        return botHome.getBooks().stream().filter(b -> b.getId() == bookId).findFirst().orElse(null);
    }

    private Reward getReward(final int rewardId) throws BotErrorException {
        for (Reward reward : botHome.getGiveaway().getRewards()) {
            System.out.println(reward.getId());
        }
        Reward reward = botHome.getGiveaway().getRewards().stream().
                filter(r -> r.getId() == rewardId).findFirst().get();
        if (reward == null) {
            throw new BotErrorException("No reward with id " + rewardId);
        }
        return reward;
    }

    private Statement addStatement(final Book book, final String text) throws BotErrorException {
        BookSerializer bookSerializer = serializers.getBookSerializer();

        Statement statement = new Statement(Statement.UNREGISTERED_ID, text);
        bookSerializer.saveStatement(book.getId(), statement);
        book.addStatement(statement);
        return statement;
    }

    private String describePlayer(final int userId) throws BotErrorException {
        User user = serializers.getUserSerializer().lookupById(userId);
        if (user.getTwitchUsername() != null) {
            return user.getTwitchUsername();
        }
        return user.getDiscordUsername();
    }
}
