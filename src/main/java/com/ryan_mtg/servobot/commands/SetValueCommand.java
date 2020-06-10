package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.commands.hierarchy.CommandSettings;
import com.ryan_mtg.servobot.commands.hierarchy.InvokedCommand;
import com.ryan_mtg.servobot.error.BotHomeError;
import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.events.CommandInvokedEvent;
import com.ryan_mtg.servobot.model.editors.StorageValueEditor;
import com.ryan_mtg.servobot.model.storage.StorageValue;

import java.util.Scanner;

public class SetValueCommand extends InvokedCommand {
    public static final CommandType TYPE = CommandType.SET_VALUE_COMMAND_TYPE;

    public SetValueCommand(final int id, final CommandSettings commandSettings) {
        super(id, commandSettings);
    }

    @Override
    public void perform(final CommandInvokedEvent event) throws BotHomeError, UserError {
        Scanner scanner = new Scanner(event.getArguments());
        // TODO: use commandParser

        String name = scanner.next();
        StorageValue.validateName(name);

        scanner.useDelimiter("\\z");

        if (!scanner.hasNext()) {
            throw new UserError("No value to set!");
        }

        String value = scanner.next().trim();

        if (value.isEmpty()) {
            throw new UserError("No value to set!");
        }

        StorageValueEditor storageValueEditor = event.getStorageValueEditor();
        StorageValue storageValue = storageValueEditor.setStorageValue(name, value);

        String response = String.format("%s set to %s.", storageValue.getName(), storageValue.getValue());
        event.say(response);
    }

    @Override
    public CommandType getType() {
        return TYPE;
    }

    @Override
    public void acceptVisitor(final CommandVisitor commandVisitor) {
        commandVisitor.visitSetValueCommand(this);
    }
}
