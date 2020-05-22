package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.commands.hierarchy.MessageCommand;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.MessageSentEvent;
import com.ryan_mtg.servobot.model.HomeEditor;
import com.ryan_mtg.servobot.model.storage.StorageValue;

import java.util.Scanner;

public class SetValueCommand extends MessageCommand {
    public static final CommandType TYPE = CommandType.SET_VALUE_COMMAND_TYPE;

    public SetValueCommand(final int id, final CommandSettings commandSettings) {
        super(id, commandSettings);
    }

    @Override
    public void perform(final MessageSentEvent event, final String arguments) throws BotErrorException {
        Scanner scanner = new Scanner(arguments);

        String name = scanner.next();
        StorageValue.validateName(name);

        scanner.useDelimiter("\\z");

        if (!scanner.hasNext()) {
            throw new BotErrorException("No value to set!");
        }

        String value = scanner.next().trim();

        if (value.isEmpty()) {
            throw new BotErrorException("No value to set!");
        }

        HomeEditor homeEditor = event.getHomeEditor();
        StorageValue storageValue = homeEditor.setStorageValue(name, value);

        String response = String.format("%s set to %s.", storageValue.getName(), storageValue.getValue());
        MessageCommand.say(event, response);
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
