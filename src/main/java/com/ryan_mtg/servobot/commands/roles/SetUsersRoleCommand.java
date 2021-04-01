package com.ryan_mtg.servobot.commands.roles;

import com.ryan_mtg.servobot.commands.CommandType;
import com.ryan_mtg.servobot.commands.CommandVisitor;
import com.ryan_mtg.servobot.commands.hierarchy.CommandSettings;
import com.ryan_mtg.servobot.commands.hierarchy.InvokedHomedCommand;
import com.ryan_mtg.servobot.error.BotHomeError;
import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.events.CommandInvokedHomeEvent;
import com.ryan_mtg.servobot.model.Role;
import com.ryan_mtg.servobot.model.ServiceHome;
import com.ryan_mtg.servobot.model.User;
import com.ryan_mtg.servobot.model.scope.SimpleSymbolTable;
import com.ryan_mtg.servobot.utility.Validation;
import lombok.Getter;

public class SetUsersRoleCommand extends InvokedHomedCommand {
    public static final CommandType TYPE = CommandType.SET_USERS_ROLE_COMMAND_TYPE;

    @Getter
    private final long setRoleId;

    @Getter
    private final long unsetRoleId;

    @Getter
    private String response;

    public SetUsersRoleCommand(final int id, final CommandSettings commandSettings, final long setRoleId,
           final long unsetRoleId, final String response) throws UserError {
        super(id, commandSettings);
        setResponse(response);
        this.setRoleId = setRoleId;
        this.unsetRoleId = unsetRoleId;
    }


    public void setResponse(final String response) throws UserError {
        Validation.validateStringLength(response, Validation.MAX_TEXT_LENGTH, "Response");
        this.response = response;
    }

    @Override
    public void perform(final CommandInvokedHomeEvent event) throws BotHomeError, UserError {
        String arguments = event.getArguments();
        ServiceHome serviceHome = event.getServiceHome();
        User user = serviceHome.getUser(arguments);
        SimpleSymbolTable symbolTable = new SimpleSymbolTable();

        if (setRoleId != 0) {
            Role role = serviceHome.getRole(setRoleId);
            symbolTable.addValue("role", role.getName());
            symbolTable.addValue("setRole", role.getName());
        } else {
            symbolTable.addValue("role", "");
            symbolTable.addValue("setRole", "");
        }

        if (unsetRoleId != 0) {
            Role role = serviceHome.getRole(unsetRoleId);
            symbolTable.addValue("unsetRole", role.getName());
        } else {
            symbolTable.addValue("unsetRole", "");
        }

        if (setRoleId != 0 && !serviceHome.hasRole(user, setRoleId)) {
            serviceHome.setRole(user, setRoleId);
        }

        if (unsetRoleId != 0 && serviceHome.hasRole(user, unsetRoleId)) {
            serviceHome.clearRole(user, unsetRoleId);
        }

        symbolTable.addValue("input", arguments);
        symbolTable.addValue("user", user.getName());
        event.say(symbolTable, response);
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