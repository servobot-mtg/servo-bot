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
import com.ryan_mtg.servobot.model.scope.SimpleSymbolTable;
import com.ryan_mtg.servobot.utility.Validation;
import lombok.Getter;

public class ArrestCommand extends InvokedHomedCommand {
    public static final CommandType TYPE = CommandType.ARREST_COMMAND_TYPE;

    @Getter
    private String prisonRole;

    @Getter
    private String message;

    public ArrestCommand(final int id, final CommandSettings commandSettings, final String prisonRole,
            final String message) throws UserError {
        super(id, commandSettings);

        this.prisonRole = prisonRole;
        this.message = message;

        Validation.validateStringLength(prisonRole, Validation.MAX_ROLE_LENGTH, "Prison role");
        Validation.validateStringLength(message, Validation.MAX_TEXT_LENGTH, "Response message");
    }

    @Override
    public CommandType getType() {
        return TYPE;
    }

    @Override
    public void acceptVisitor(final CommandVisitor commandVisitor) {
        commandVisitor.visitArrestCommand(this);
    }

    @Override
    public void perform(final CommandInvokedHomeEvent event) throws BotHomeError, UserError {
        ServiceHome serviceHome = event.getServiceHome();
        User cop  = event.getSender();

        if (JailUtility.isInAnyJail(serviceHome, cop, prisonRole)) {
            event.say("You can't arrest someone while in jail!");
            return;
        }

        String arguments = event.getArguments();
        if (!serviceHome.hasUser(arguments)) {
            if (serviceHome.hasRole(arguments)) {
                serviceHome.setRole(cop, prisonRole);
                event.say("No one is above the law! %sender% is going to the clink.");
                return;
            }
            event.say(String.format("No user with name %s.", arguments));
            return;
        }

        User criminal = serviceHome.getUser(arguments);
        if (serviceHome.isHigherRanked(criminal, cop)) {
            serviceHome.setRole(event.getSender(), prisonRole);
            event.say("I see through your tricks! I'm checking %sender% into the greybar hotel.");
            return;
        }

        if (JailUtility.isInAnyJail(serviceHome, criminal, prisonRole)) {
            event.say(String.format("%s is already in jail!", criminal.getName()));
            return;
        }

        serviceHome.setRole(criminal, prisonRole);

        SimpleSymbolTable symbolTable = new SimpleSymbolTable();
        symbolTable.addValue("input", arguments);
        symbolTable.addValue("criminal", criminal.getName());
        symbolTable.addValue("cop", cop.getName());
        symbolTable.addValue("role", prisonRole);

        event.say(symbolTable, message);
    }
}
