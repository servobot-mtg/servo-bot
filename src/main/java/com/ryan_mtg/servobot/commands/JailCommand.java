package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.MessageSentEvent;
import com.ryan_mtg.servobot.model.HomeEditor;
import com.ryan_mtg.servobot.model.User;
import com.ryan_mtg.servobot.model.storage.IntegerStorageValue;

public class JailCommand extends MessageCommand {
    public static final int TYPE = 25;
    private String prisonRole;
    private int threshold;
    private String variableName;

    public JailCommand(final int id, final int flags, final Permission permission, final String prisonRole,
                       final int threshold, final String variableName) {
        super(id, flags, permission);
        this.threshold = threshold;
        this.prisonRole = prisonRole;
        this.variableName = variableName;
    }

    public String getPrisonRole() {
        return prisonRole;
    }

    public int getThreshold() {
        return threshold;
    }

    public String getVariableName() {
        return variableName;
    }

    @Override
    public int getType() {
        return TYPE;
    }

    @Override
    public void acceptVisitor(final CommandVisitor commandVisitor) {
        commandVisitor.visitJailCommand(this);
    }

    @Override
    public void perform(final MessageSentEvent event, final String arguments) throws BotErrorException {
        HomeEditor homeEditor = event.getHomeEditor();
        User sender = event.getSender();
        IntegerStorageValue storageValue =
                homeEditor.incrementStorageValue(sender.getHomedUser().getId(), variableName, 0);

        if (storageValue.getValue() >= threshold) {
            event.getHome().setRole(sender, prisonRole);
            MessageCommand.say(event, sender.getName() + ", I'm throwing the book at you!");
        }
    }
}
