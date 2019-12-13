package com.ryan_mtg.servobot.model;

import com.ryan_mtg.servobot.commands.Command;
import com.ryan_mtg.servobot.commands.CommandAlert;
import com.ryan_mtg.servobot.commands.CommandAlias;
import com.ryan_mtg.servobot.commands.CommandEvent;
import com.ryan_mtg.servobot.commands.CommandTable;
import com.ryan_mtg.servobot.commands.CommandTableEdit;
import com.ryan_mtg.servobot.commands.MessageCommand;
import com.ryan_mtg.servobot.commands.Permission;
import com.ryan_mtg.servobot.data.factories.BookSerializer;
import com.ryan_mtg.servobot.data.factories.SerializerContainer;
import com.ryan_mtg.servobot.data.models.BotHomeRow;
import com.ryan_mtg.servobot.data.models.SuggestionRow;
import com.ryan_mtg.servobot.data.repositories.BotHomeRepository;
import com.ryan_mtg.servobot.data.repositories.SuggestionRepository;
import com.ryan_mtg.servobot.events.AlertEvent;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.BotHomeAlertEvent;
import com.ryan_mtg.servobot.reaction.Reaction;
import com.ryan_mtg.servobot.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.transaction.Transactional;
import java.util.List;

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

    @Transactional
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

    public boolean secureCommand(int commandId, boolean secure) {
        Command command = botHome.getCommandTable().secureCommand(commandId, secure);
        serializers.getCommandSerializer().saveCommand(botHome.getId(), command);
        return command.isSecure();
    }

    public boolean secureReaction(int reactionId, boolean secure) {
        Reaction reaction = botHome.getReactionTable().secureReaction(reactionId, secure);
        serializers.getReactionSerializer().saveReaction(botHome.getId(), reaction);
        return reaction.isSecure();
    }

    public void addCommand(final String alias, final MessageCommand command) {
        CommandTable commandTable = botHome.getCommandTable();

        CommandTableEdit commandTableEdit = commandTable.addCommand(alias, command);
        serializers.getCommandTableSerializer().commit(botHome.getId(), commandTableEdit);
    }

    public void deleteCommand(final String commandName) throws BotErrorException {
        CommandTable commandTable = botHome.getCommandTable();
        CommandTableEdit commandTableEdit = commandTable.deleteCommand(commandName);

        if (commandTableEdit.getDeletedCommands().isEmpty()) {
            throw new BotErrorException(String.format("Command '%s' not found.", commandName));
        }
        serializers.getCommandTableSerializer().commit(botHome.getId(), commandTableEdit);
    }

    public void deleteCommand(final int commandId) throws BotErrorException {
        CommandTable commandTable = botHome.getCommandTable();
        CommandTableEdit commandTableEdit = commandTable.deleteCommand(commandId);

        if (commandTableEdit.getDeletedCommands().isEmpty()) {
            throw new BotErrorException(String.format("Command '%d' not found.", commandId));
        }
        serializers.getCommandTableSerializer().commit(botHome.getId(), commandTableEdit);
    }

    public void deleteAlias(final int aliasId) throws BotErrorException {
        CommandTable commandTable = botHome.getCommandTable();
        CommandAlias commandAlias = serializers.getCommandSerializer().getAlias(aliasId);
        CommandTableEdit commandTableEdit = commandTable.deleteAlias(commandAlias);

        if (commandTableEdit.getDeletedAliases().isEmpty()) {
            throw new BotErrorException(String.format("Alias '%d' not found.", aliasId));
        }
        serializers.getCommandTableSerializer().commit(botHome.getId(), commandTableEdit);
    }

    public void deleteEvent(final int eventId) throws BotErrorException {
        CommandTable commandTable = botHome.getCommandTable();
        CommandEvent commandEvent = serializers.getCommandSerializer().getCommandEvent(eventId);
        CommandTableEdit commandTableEdit = commandTable.deleteEvent(commandEvent);

        if (commandTableEdit.getDeletedEvents().isEmpty()) {
            throw new BotErrorException(String.format("Event '%d' not found.", eventId));
        }
        serializers.getCommandTableSerializer().commit(botHome.getId(), commandTableEdit);
    }

    public void deleteAlert(final int alertId) throws BotErrorException {
        CommandTable commandTable = botHome.getCommandTable();
        CommandAlert commandAlert = serializers.getCommandSerializer().getCommandAlert(alertId);
        CommandTableEdit commandTableEdit = commandTable.deleteAlert(commandAlert);

        if (commandTableEdit.getDeletedAlerts().isEmpty()) {
            throw new BotErrorException(String.format("Alert '%d' not found.", alertId));
        }
        serializers.getCommandTableSerializer().commit(botHome.getId(), commandTableEdit);
    }

    public void deleteStatement(final int bookId, final int statementId) {
        botHome.getBooks().stream().filter(book -> book.getId() == bookId).forEach(book -> {
            book.deleteStatement(statementId);
            serializers.getStatementRepository().deleteById(statementId);
        });
    }

    public void modifyStatement(final int bookId, final int statementId, final String text) {
        BookSerializer bookSerializer = serializers.getBookSerializer();
        botHome.getBooks().stream().filter(book -> book.getId() == bookId).forEach(book -> {
            book.getStatements().stream().filter(statement -> statement.getId() ==statementId).forEach(statement -> {
                statement.setText(text);
                bookSerializer.saveStatement(bookId, statement);
            });
        });
    }

    public void alert(final String alertToken) {
        Home home = new MultiServiceHome(botHome.getServiceHomes(), this);
        AlertEvent alertEvent = new BotHomeAlertEvent(botHome.getId(), alertToken, home);
        botHome.getListener().onAlert(alertEvent);
    }

    public Permission setCommandPermission(final int commandId, final Permission permission) {
        Command command = botHome.getCommandTable().setCommandPermission(commandId, permission);
        serializers.getCommandSerializer().saveCommand(botHome.getId(), command);
        return command.getPermission();
    }

    @Transactional
    public void addSuggestion(final String command) {
        String alias = command.toLowerCase();
        SuggestionRepository suggestionRepository = serializers.getSuggestionRepository();
        SuggestionRow suggestionRow = suggestionRepository.findByAlias(alias);
        if (suggestionRow == null) {
            suggestionRow = new SuggestionRow(alias, 1);
        } else {
            suggestionRow.setCount(suggestionRow.getCount() + 1);
        }
        suggestionRepository.save(suggestionRow);
    }

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

    public void setGameQueueName(final int gameQueueId, final String name) throws BotErrorException {
        GameQueue gameQueue = getGameQueue(gameQueueId);

        if (name.equals(gameQueue.getName())) {
            return;
        }

        gameQueue.setName(name);

        serializers.getGameQueueSerializer().saveGameQueue(gameQueue);
    }

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

    private String describePlayer(final int userId) {
        User user = serializers.getUserSerializer().lookupById(userId);
        if (user.getTwitchUsername() != null) {
            return user.getTwitchUsername();
        }
        return user.getDiscordUsername();
    }

    private GameQueue getGameQueue(final int gameQueueId) throws BotErrorException {
        GameQueue gameQueue = botHome.getGameQueue(gameQueueId);
        if (gameQueue == null) {
            throw new BotErrorException("No Game Queue");
        }
        return gameQueue;
    }
}
