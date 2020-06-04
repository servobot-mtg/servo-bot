package com.ryan_mtg.servobot.model.editors;

import com.ryan_mtg.servobot.commands.CommandTable;
import com.ryan_mtg.servobot.commands.CommandTableEdit;
import com.ryan_mtg.servobot.commands.hierarchy.Command;
import com.ryan_mtg.servobot.commands.hierarchy.InvokedCommand;
import com.ryan_mtg.servobot.data.factories.CommandTableSerializer;
import com.ryan_mtg.servobot.events.BotErrorException;

import javax.transaction.Transactional;

public class CommandTableEditor {
    private CommandTable commandTable;
    private CommandTableSerializer commandTableSerializer;

    public CommandTableEditor(final CommandTable commandTable, final CommandTableSerializer commandTableSerializer) {
        this.commandTable = commandTable;
        this.commandTableSerializer = commandTableSerializer;
    }

    @Transactional(rollbackOn = BotErrorException.class)
    public boolean addCommand(final String alias, final InvokedCommand command) throws BotErrorException {
        CommandTableEdit commandTableEdit = commandTable.addCommand(alias, command);
        boolean added = commandTableEdit.getDeletedTriggers().isEmpty();
        commandTableSerializer.commit(commandTableEdit);
        return added;
    }

    @Transactional(rollbackOn = BotErrorException.class)
    public boolean aliasCommand(final String newAlias, final String existingAlias) throws BotErrorException {
        CommandTableEdit commandTableEdit = commandTable.addAlias(newAlias, existingAlias);
        boolean added = commandTableEdit.getDeletedTriggers().isEmpty();
        commandTableSerializer.commit(commandTableEdit);
        return added;
    }

    @Transactional(rollbackOn = BotErrorException.class)
    public void deleteCommand(final com.ryan_mtg.servobot.model.User deleter, final String commandName)
            throws BotErrorException {
        Command command = commandTable.getCommand(commandName);

        if (command == null) {
            throw new BotErrorException(String.format("No command named '%s.'", commandName));
        }

        if (!command.hasPermissions(deleter)) {
            throw new BotErrorException(
                    String.format("%s is not allowed to delete '%s.'", deleter.getName(), commandName));
        }

        CommandTableEdit commandTableEdit = commandTable.deleteCommand(commandName);
        commandTableSerializer.commit(commandTableEdit);
    }
}
