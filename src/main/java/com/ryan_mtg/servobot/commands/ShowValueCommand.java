package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.commands.hierarchy.CommandSettings;
import com.ryan_mtg.servobot.commands.hierarchy.InvokedCommand;
import com.ryan_mtg.servobot.error.BotHomeError;
import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.events.CommandInvokedEvent;
import com.ryan_mtg.servobot.model.editors.StorageValueEditor;
import com.ryan_mtg.servobot.model.storage.StorageValue;
import com.ryan_mtg.servobot.utility.Strings;

public class ShowValueCommand extends InvokedCommand {
    public static final CommandType TYPE = CommandType.SHOW_VALUE_COMMAND_TYPE;

    public ShowValueCommand(final int id, final CommandSettings commandSettings) {
        super(id, commandSettings);
    }

    @Override
    public void perform(final CommandInvokedEvent event) throws BotHomeError, UserError {
        String arguments = event.getArguments();
        if (Strings.isBlank(arguments)) {
            throw new UserError("No value name given to show value command.");
        }
        StorageValueEditor storageValueEditor = event.getStorageValueEditor();
        StorageValue storageValue = storageValueEditor.getStorageValue(arguments);
        event.say(String.format("The value of %s is %s.", storageValue.getName(), storageValue.getValue()));
    }

    @Override
    public CommandType getType() {
        return TYPE;
    }

    @Override
    public void acceptVisitor(final CommandVisitor commandVisitor) {
        commandVisitor.visitShowValueCommand(this);
    }
}
