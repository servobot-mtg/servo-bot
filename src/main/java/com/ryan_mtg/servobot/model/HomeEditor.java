package com.ryan_mtg.servobot.model;

import com.ryan_mtg.servobot.commands.Command;
import com.ryan_mtg.servobot.commands.CommandTable;
import com.ryan_mtg.servobot.commands.CommandTableEdit;
import com.ryan_mtg.servobot.commands.MessageCommand;
import com.ryan_mtg.servobot.commands.Permission;
import com.ryan_mtg.servobot.data.factories.BookSerializer;
import com.ryan_mtg.servobot.data.factories.SerializerContainer;
import com.ryan_mtg.servobot.data.models.BotHomeRow;
import com.ryan_mtg.servobot.data.models.StatementRow;
import com.ryan_mtg.servobot.data.models.SuggestionRow;
import com.ryan_mtg.servobot.data.repositories.BookRepository;
import com.ryan_mtg.servobot.data.repositories.BotHomeRepository;
import com.ryan_mtg.servobot.data.repositories.StatementRepository;
import com.ryan_mtg.servobot.data.repositories.SuggestionRepository;
import com.ryan_mtg.servobot.events.AlertEvent;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.BotHomeAlertEvent;
import com.ryan_mtg.servobot.reaction.Reaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.transaction.Transactional;

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
}
