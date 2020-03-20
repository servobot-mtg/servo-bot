package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.MessageSentEvent;
import com.ryan_mtg.servobot.model.HomeEditor;
import com.ryan_mtg.servobot.model.User;
import com.ryan_mtg.servobot.model.storage.IntegerStorageValue;
import com.ryan_mtg.servobot.utility.Validation;
import lombok.Getter;

public class JailCommand extends MessageCommand {
    public static final int TYPE = 25;

    @Getter
    private String prisonRole;

    @Getter
    private int threshold;

    @Getter
    private String variableName;

    public JailCommand(final int id, final int flags, final Permission permission, final String prisonRole,
                       final int threshold, final String variableName) throws BotErrorException {
        super(id, flags, permission);
        this.threshold = threshold;
        this.prisonRole = prisonRole;
        this.variableName = variableName;

        Validation.validateStringLength(prisonRole, Validation.MAX_ROLE_LENGTH, "Prison role");
        Validation.validateStringLength(variableName, Validation.MAX_STORAGE_NAME_LENGTH, "Threshold variable name");
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

        int incrementedValue = storageValue.getValue();

        if (incrementedValue >= threshold) {
            event.getHome().setRole(sender, prisonRole);
        }

        if (incrementedValue == threshold) {
            MessageCommand.say(event, sender.getName() + ", I'm throwing the book at you!");
        } else if (incrementedValue == threshold + 1) {
            MessageCommand.say(event, sender.getName() + ", you have the right to remain silent!");
        } else if (incrementedValue == threshold + 2) {
            MessageCommand.say(event,
                    "You got to ask yourself one question, \"Do I feel lucky?\" Well, do you, punk? ");
        }
    }
}
