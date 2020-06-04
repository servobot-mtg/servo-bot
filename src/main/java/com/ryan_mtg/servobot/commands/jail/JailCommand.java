package com.ryan_mtg.servobot.commands.jail;

import com.ryan_mtg.servobot.commands.hierarchy.CommandSettings;
import com.ryan_mtg.servobot.commands.CommandType;
import com.ryan_mtg.servobot.commands.CommandVisitor;
import com.ryan_mtg.servobot.commands.hierarchy.InvokedHomedCommand;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.CommandInvokedHomeEvent;
import com.ryan_mtg.servobot.model.User;
import com.ryan_mtg.servobot.model.scope.SimpleSymbolTable;
import com.ryan_mtg.servobot.utility.Validation;
import lombok.Getter;

public class JailCommand extends InvokedHomedCommand {
    public static final CommandType TYPE = CommandType.JAIL_COMMAND_TYPE;

    @Getter
    private String prisonRole;

    @Getter
    private int threshold;

    @Getter
    private String variableName;

    public JailCommand(final int id, final CommandSettings commandSettings, final String prisonRole,
                       final int threshold, final String variableName) throws BotErrorException {
        super(id, commandSettings);
        this.threshold = threshold;
        this.prisonRole = prisonRole;
        this.variableName = variableName;

        Validation.validateStringLength(prisonRole, Validation.MAX_ROLE_LENGTH, "Prison role");
        Validation.validateStringLength(variableName, Validation.MAX_STORAGE_NAME_LENGTH, "Threshold variable name");
    }

    @Override
    public CommandType getType() {
        return TYPE;
    }

    @Override
    public void acceptVisitor(final CommandVisitor commandVisitor) {
        commandVisitor.visitJailCommand(this);
    }

    @Override
    public void perform(final CommandInvokedHomeEvent event) throws BotErrorException {
        User sender = event.getSender();

        if (JailUtility.isInJail(event.getHome(), sender, prisonRole)) {
            return;
        }

        SimpleSymbolTable symbolTable = new SimpleSymbolTable();
        symbolTable.addValue("criminal", sender.getName());
        symbolTable.addValue("cop", event.getHome().getBotName());
        symbolTable.addValue("role", prisonRole);

        event.getHome().setRole(sender, prisonRole);
        event.say(symbolTable, "%criminal%, I'm throwing the book at you!");
    }
}
