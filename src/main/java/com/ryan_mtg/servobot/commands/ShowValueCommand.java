package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.MessageSentEvent;
import com.ryan_mtg.servobot.model.HomeEditor;
import com.ryan_mtg.servobot.model.storage.StorageValue;

public class ShowValueCommand extends MessageCommand {
    public static final int TYPE = 17;

    public ShowValueCommand(final int id, final CommandSettings commandSettings) {
        super(id, commandSettings);
    }

    @Override
    public void perform(final MessageSentEvent event, final String arguments) throws BotErrorException {
        if (arguments == null) {
            throw new BotErrorException("No value name given to show value command.");
        }
        HomeEditor homeEditor = event.getHomeEditor();
        StorageValue storageValue = homeEditor.getStorageValue(arguments);
        String text = String.format("The value of %s is %s.", storageValue.getName(), storageValue.getValue());
        MessageCommand.say(event, text);
    }

    @Override
    public int getType() {
        return TYPE;
    }

    @Override
    public void acceptVisitor(final CommandVisitor commandVisitor) {
        commandVisitor.visitShowValueCommand(this);
    }
}
