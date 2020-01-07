package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.MessageSentEvent;
import com.ryan_mtg.servobot.model.HomeEditor;
import com.ryan_mtg.servobot.model.storage.StorageValue;

import java.util.Scanner;

public class SetValueCommand extends MessageCommand {
    public static final int TYPE = 18;

    public SetValueCommand(final int id, final boolean secure, final Permission permission) {
        super(id, secure, permission);
    }

    @Override
    public void perform(final MessageSentEvent event, final String arguments) throws BotErrorException {
        Scanner scanner = new Scanner(arguments);

        String name = scanner.next();
        if (!StorageValue.STORAGE_VALUE_NAME_PATTERN.matcher(name).matches()) {
            throw new BotErrorException(String.format("%s doesn't look like a value name.", name));
        }

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
    public int getType() {
        return TYPE;
    }

    @Override
    public void acceptVisitor(final CommandVisitor commandVisitor) {
        commandVisitor.visitSetValueCommand(this);
    }
}
