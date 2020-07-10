package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.commands.hierarchy.CommandSettings;
import com.ryan_mtg.servobot.commands.hierarchy.InvokedHomedCommand;
import com.ryan_mtg.servobot.error.BotHomeError;
import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.events.CommandInvokedHomeEvent;
import com.ryan_mtg.servobot.model.ServiceHome;
import com.ryan_mtg.servobot.model.User;
import com.ryan_mtg.servobot.model.scope.SimpleSymbolTable;
import com.ryan_mtg.servobot.utility.Validation;
import lombok.Getter;

public class SetUsersRoleCommand extends InvokedHomedCommand {
    public static final CommandType TYPE = CommandType.SET_USERS_ROLE_COMMAND_TYPE;

    @Getter
    private String role;

    @Getter
    private String message;

    public SetUsersRoleCommand(final int id, final CommandSettings commandSettings, final String role,
            final String message) throws UserError {
        super(id, commandSettings);
        this.role = role;
        this.message = message;

        Validation.validateStringLength(role, Validation.MAX_ROLE_LENGTH, "Role");
    }

    @Override
    public void perform(final CommandInvokedHomeEvent event) throws BotHomeError, UserError {
        String arguments = event.getArguments();
        ServiceHome serviceHome = event.getServiceHome();
        User user = serviceHome.getUser(arguments);

        if (!serviceHome.hasRole(user, role)) {
            serviceHome.setRole(user, role);

            SimpleSymbolTable symbolTable = new SimpleSymbolTable();
            symbolTable.addValue("input", arguments);
            symbolTable.addValue("user", user.getName());
            symbolTable.addValue("role", role);

            event.say(symbolTable, message);
        }
    }

    @Override
    public CommandType getType() {
        return TYPE;
    }

    @Override
    public void acceptVisitor(final CommandVisitor commandVisitor) {
        commandVisitor.visitSetUsersRoleCommand(this);
    }
}
