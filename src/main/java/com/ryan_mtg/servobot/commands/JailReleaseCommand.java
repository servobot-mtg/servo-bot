package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.MessageSentEvent;
import com.ryan_mtg.servobot.model.Home;
import com.ryan_mtg.servobot.model.User;
import com.ryan_mtg.servobot.utility.Validation;
import lombok.Getter;

public class JailReleaseCommand extends MessageCommand {
    public static final int TYPE = 29;

    @Getter
    private String prisonRole;

    public JailReleaseCommand(final int id, final int flags, final Permission permission, final String prisonRole)
            throws BotErrorException {
        super(id, flags, permission);
        this.prisonRole = prisonRole;

        Validation.validateStringLength(prisonRole, Validation.MAX_ROLE_LENGTH, "Prison role");
    }

    @Override
    public int getType() {
        return TYPE;
    }

    @Override
    public void acceptVisitor(final CommandVisitor commandVisitor) {
        commandVisitor.visitJailReleaseCommand(this);
    }

    @Override
    public void perform(final MessageSentEvent event, final String arguments) throws BotErrorException {
        Home home = event.getHome();
        User releaser = event.getSender();
        User jailee = home.getUser(arguments);

        if (home.hasRole(releaser, prisonRole)) {
            MessageCommand.say(event, String.format("You can't release someone while in %s!", prisonRole));
            return;
        }

        if (!home.hasRole(jailee, prisonRole)) {
            MessageCommand.say(event, String.format("%s is not in %s!", jailee.getName(), prisonRole));
            return;
        }

        home.clearRole(jailee, prisonRole);
        MessageCommand.say(event,
                String.format("%s broke %s out of %s!", releaser.getName(), jailee.getName(), prisonRole));
    }
}
