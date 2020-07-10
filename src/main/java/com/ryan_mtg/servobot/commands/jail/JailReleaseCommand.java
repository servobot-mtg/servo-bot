package com.ryan_mtg.servobot.commands.jail;

import com.ryan_mtg.servobot.commands.hierarchy.CommandSettings;
import com.ryan_mtg.servobot.commands.CommandType;
import com.ryan_mtg.servobot.commands.CommandVisitor;
import com.ryan_mtg.servobot.commands.hierarchy.InvokedHomedCommand;
import com.ryan_mtg.servobot.error.BotHomeError;
import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.events.CommandInvokedHomeEvent;
import com.ryan_mtg.servobot.model.ServiceHome;
import com.ryan_mtg.servobot.model.User;
import com.ryan_mtg.servobot.utility.Validation;
import lombok.Getter;

public class JailReleaseCommand extends InvokedHomedCommand {
    public static final CommandType TYPE = CommandType.JAIL_RELEASE_COMMAND_TYPE;

    @Getter
    private String prisonRole;

    public JailReleaseCommand(final int id, final CommandSettings commandSettings, final String prisonRole)
            throws UserError {
        super(id, commandSettings);
        this.prisonRole = prisonRole;

        Validation.validateStringLength(prisonRole, Validation.MAX_ROLE_LENGTH, "Prison role");
    }

    @Override
    public CommandType getType() {
        return TYPE;
    }

    @Override
    public void acceptVisitor(final CommandVisitor commandVisitor) {
        commandVisitor.visitJailReleaseCommand(this);
    }

    @Override
    public void perform(final CommandInvokedHomeEvent event) throws BotHomeError, UserError {
        ServiceHome serviceHome = event.getServiceHome();
        User releaser = event.getSender();
        User jailee = serviceHome.getUser(event.getArguments());

        if (JailUtility.isInJail(serviceHome, releaser, prisonRole)) {
            event.say(String.format("You can't release someone while in %s!", prisonRole));
            return;
        }

        if (!JailUtility.isInJail(serviceHome, jailee, prisonRole)) {
            event.say(String.format("%s is not in %s!", jailee.getName(), prisonRole));
            return;
        }

        if (serviceHome.isHigherRanked(jailee, releaser)) {
            serviceHome.setRole(event.getSender(), prisonRole);
            event.say("I see through your tricks! I'm checking %sender% into the greybar hotel.");
            return;
        }

        serviceHome.clearRole(jailee, prisonRole);
        event.say(String.format("%s broke %s out of %s!", releaser.getName(), jailee.getName(), prisonRole));
    }
}
