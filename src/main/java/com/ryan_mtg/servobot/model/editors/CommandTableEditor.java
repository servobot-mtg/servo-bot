package com.ryan_mtg.servobot.model.editors;

import com.ryan_mtg.servobot.commands.CommandTable;
import com.ryan_mtg.servobot.commands.CommandTableEdit;
import com.ryan_mtg.servobot.commands.CommandType;
import com.ryan_mtg.servobot.commands.Permission;
import com.ryan_mtg.servobot.commands.hierarchy.Command;
import com.ryan_mtg.servobot.commands.hierarchy.InvokedCommand;
import com.ryan_mtg.servobot.commands.trigger.Trigger;
import com.ryan_mtg.servobot.controllers.CommandDescriptor;
import com.ryan_mtg.servobot.data.factories.CommandSerializer;
import com.ryan_mtg.servobot.data.factories.CommandTableSerializer;
import com.ryan_mtg.servobot.data.models.CommandRow;
import com.ryan_mtg.servobot.error.LibraryError;
import com.ryan_mtg.servobot.error.SystemError;
import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.model.books.BookTable;
import com.ryan_mtg.servobot.model.game_queue.Game;
import com.ryan_mtg.servobot.user.HomedUser;
import com.ryan_mtg.servobot.user.User;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

public class CommandTableEditor {
    private final BookTable bookTable;
    private final CommandTable commandTable;
    private final CommandSerializer commandSerializer;
    private final CommandTableSerializer commandTableSerializer;
    private final GameQueueEditor gameQueueEditor;

    public CommandTableEditor(final BookTable bookTable, final CommandTable commandTable,
            final CommandSerializer commandSerializer, final CommandTableSerializer commandTableSerializer,
            final GameQueueEditor gameQueueEditor) {
        this.bookTable = bookTable;
        this.commandTable = commandTable;
        this.commandSerializer = commandSerializer;
        this.commandTableSerializer = commandTableSerializer;
        this.gameQueueEditor = gameQueueEditor;
    }

    @Transactional(rollbackOn = Exception.class)
    public CommandDescriptor addCommand(final CommandRow commandRow) throws UserError {
        if (commandRow.getType() == CommandType.GAME_QUEUE_COMMAND_TYPE.getType())  {
            Game game = Game.get((int)(long) commandRow.getLongParameter());

            gameQueueEditor.createGameQueue(game,
                    savedGameQueue -> commandRow.setLongParameter(savedGameQueue.getId()));
        }

        Command command = commandSerializer.createCommand(commandRow, bookTable.getBookMap());
        CommandTableEdit commandTableEdit = commandTable.addCommand(command);
        commandTableSerializer.commit(commandTableEdit);
        return new CommandDescriptor(command);
    }

    @Transactional(rollbackOn = Exception.class)
    public boolean addCommand(final String alias, final InvokedCommand command) throws UserError {
        CommandTableEdit commandTableEdit = commandTable.addCommand(alias, command);
        boolean added = commandTableEdit.getDeletedTriggers().isEmpty();
        commandTableSerializer.commit(commandTableEdit);
        return added;
    }

    @Transactional(rollbackOn = Exception.class)
    public boolean aliasCommand(final String newAlias, final String existingAlias) throws UserError {
        CommandTableEdit commandTableEdit = commandTable.addAlias(newAlias, existingAlias);
        boolean added = commandTableEdit.getDeletedTriggers().isEmpty();
        commandTableSerializer.commit(commandTableEdit);
        return added;
    }

    @Transactional(rollbackOn = Exception.class)
    public void deleteCommand(final com.ryan_mtg.servobot.model.User deleter, final String commandName)
            throws UserError {
        Command command = commandTable.getCommand(commandName);

        if (command == null) {
            throw new UserError("No command named '%s.'", commandName);
        }

        if (!command.hasPermissions(deleter)) {
            throw new UserError("%s is not allowed to delete '%s.'", deleter.getName(), commandName);
        }

        CommandTableEdit commandTableEdit = commandTable.deleteCommand(commandName);
        commandTableSerializer.commit(commandTableEdit);
    }

    @Transactional(rollbackOn = Exception.class)
    public void deleteCommand(final int commandId) throws LibraryError {
        CommandTableEdit commandTableEdit = commandTable.deleteCommand(commandId);
        if (commandTableEdit.getDeletedCommands().isEmpty()) {
            throw new LibraryError("Command '%d' not found.", commandId);
        }
        commandTableSerializer.commit(commandTableEdit);
    }

    @Transactional(rollbackOn = Exception.class)
    public boolean secureCommand(final int commandId, final boolean secure) {
        Command command = commandTable.secureCommand(commandId, secure);
        saveCommand(command);
        return command.isSecure();
    }

    @Transactional(rollbackOn = Exception.class)
    public boolean setCommandService(final int commandId, final int serviceType, final boolean value) {
        Command command = commandTable.getCommand(commandId);
        command.setService(serviceType, value);
        saveCommand(command);
        return command.getService(serviceType);
    }

    @Transactional(rollbackOn = Exception.class)
    public Permission setCommandPermission(final HomedUser homedUser, final int commandId, final Permission permission)
            throws UserError {
        Command command = commandTable.getCommand(commandId);
        if (!command.hasPermissions(homedUser)) {
            throw new UserError("You do not have permission to change the command's permission");
        }
        command.setPermission(permission);
        saveCommand(command);
        return command.getPermission();
    }

    @Transactional(rollbackOn = Exception.class)
    public Permission setCommandPermission(final User user, final int commandId, final Permission permission)
            throws UserError {
        Command command = commandTable.getCommand(commandId);
        if (!command.hasPermissions(user)) {
            throw new UserError("You do not have permission to change the command's permission");
        }
        command.setPermission(permission);
        saveCommand(command);
        return command.getPermission();
    }

    @Transactional(rollbackOn = Exception.class)
    public boolean setCommandOnlyWhileStreaming(final int commandId, final boolean isOnlyWhileStreaming) {
        Command command = commandTable.getCommand(commandId);
        command.setOnlyWhileStreaming(isOnlyWhileStreaming);
        saveCommand(command);
        return command.isOnlyWhileStreaming();
    }

    @Transactional(rollbackOn = Exception.class)
    public List<Trigger> addTrigger(final int commandId, final int triggerType, final String text) throws UserError {
        CommandTableEdit commandTableEdit = commandTable.addTrigger(commandId, triggerType, text);

        if (commandTableEdit.getSavedTriggers().size() != 1) {
            throw new SystemError("Trigger '%s' not added.", text);
        }

        commandTableSerializer.commit(commandTableEdit);
        Trigger trigger = commandTableEdit.getSavedTriggers().keySet().iterator().next();
        List<Trigger> response = new ArrayList<>();
        response.add(trigger);
        if (!commandTableEdit.getDeletedTriggers().isEmpty()) {
            Trigger deletedTrigger = commandTableEdit.getDeletedTriggers().get(0);
            response.add(deletedTrigger);
        }
        return response;
    }

    @Transactional(rollbackOn = Exception.class)
    public void deleteTrigger(final int triggerId) {
        CommandTableEdit commandTableEdit = commandTable.deleteTrigger(triggerId);

        if (commandTableEdit.getDeletedTriggers().isEmpty()) {
            throw new SystemError("Trigger with id '%d' not found.", triggerId);
        }
        commandTableSerializer.commit(commandTableEdit);
    }

    private void saveCommand(final Command command) {
        CommandTableEdit commandTableEdit = new CommandTableEdit();
        commandTableEdit.save(commandTable.getContextId(), command);
        commandTableSerializer.commit(commandTableEdit);
    }
}
