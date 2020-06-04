package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.commands.hierarchy.CommandSettings;
import com.ryan_mtg.servobot.commands.hierarchy.InvokedCommand;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.CommandInvokedEvent;
import com.ryan_mtg.servobot.model.editors.StorageValueEditor;
import com.ryan_mtg.servobot.model.storage.StorageValue;

public class ShowValueCommand extends InvokedCommand {
    public static final CommandType TYPE = CommandType.SHOW_VALUE_COMMAND_TYPE;

    public ShowValueCommand(final int id, final CommandSettings commandSettings) {
        super(id, commandSettings);
    }

    @Override
    public void perform(final CommandInvokedEvent event) throws BotErrorException {
        String arguments = event.getArguments();
        if (arguments == null) {
            throw new BotErrorException("No value name given to show value command.");
        }
        StorageValueEditor storageValueEditor = event.getStorageValueEditor();
        StorageValue storageValue = storageValueEditor.getStorageValue(arguments);
        String text = String.format("The value of %s is %s.", storageValue.getName(), storageValue.getValue());
        event.say(text);
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
