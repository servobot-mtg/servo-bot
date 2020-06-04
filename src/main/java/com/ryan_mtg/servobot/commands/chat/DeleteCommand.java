package com.ryan_mtg.servobot.commands.chat;

import com.ryan_mtg.servobot.commands.hierarchy.CommandSettings;
import com.ryan_mtg.servobot.commands.CommandType;
import com.ryan_mtg.servobot.commands.CommandVisitor;
import com.ryan_mtg.servobot.commands.hierarchy.InvokedCommand;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.CommandInvokedEvent;
import com.ryan_mtg.servobot.model.editors.CommandTableEditor;
import com.ryan_mtg.servobot.utility.Strings;

public class DeleteCommand extends InvokedCommand {
    public static final CommandType TYPE = CommandType.DELETE_COMMAND_TYPE;

    public DeleteCommand(final int id, final CommandSettings commandSettings) {
        super(id, commandSettings);
    }

    @Override
    public void perform(final CommandInvokedEvent event) throws BotErrorException {
        String arguments = event.getArguments();
        if (Strings.isBlank(arguments)) {
            throw new BotErrorException("Missing command name.");
        }

        if (arguments.charAt(0) != '!') {
            throw new BotErrorException("Commands must start with '!'");
        }

        String commandName = arguments.substring(1);

        if (commandName.isEmpty()) {
            throw new BotErrorException("Missing command name.");
        }

        CommandTableEditor commandTableEditor = event.getCommandTableEditor();
        commandTableEditor.deleteCommand(event.getSender(), commandName);

        event.say(String.format("Command %s deleted.", commandName));
    }

    @Override
    public CommandType getType() {
        return TYPE;
    }

    @Override
    public void acceptVisitor(final CommandVisitor commandVisitor) {
        commandVisitor.visitDeleteCommand(this);
    }
}
