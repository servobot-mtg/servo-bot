package com.ryan_mtg.servobot.commands.jail;

import com.ryan_mtg.servobot.commands.hierarchy.CommandSettings;
import com.ryan_mtg.servobot.commands.CommandType;
import com.ryan_mtg.servobot.commands.CommandVisitor;
import com.ryan_mtg.servobot.commands.hierarchy.InvokedHomedCommand;
import com.ryan_mtg.servobot.error.BotHomeError;
import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.events.CommandInvokedHomeEvent;
import com.ryan_mtg.servobot.model.HomeEditor;
import com.ryan_mtg.servobot.model.User;
import com.ryan_mtg.servobot.utility.Strings;
import com.ryan_mtg.servobot.utility.Validation;
import lombok.Getter;

import java.util.List;

public class JailBreakCommand extends InvokedHomedCommand {
    public static final CommandType TYPE = CommandType.JAIL_BREAK_COMMAND_TYPE;

    @Getter
    private String prisonRole;

    @Getter
    private String variableName;

    public JailBreakCommand(final int id, final CommandSettings commandSettings, final String prisonRole,
            final String variableName) throws UserError {
        super(id, commandSettings);

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
        commandVisitor.visitJailBreakCommand(this);
    }

    @Override
    public void perform(final CommandInvokedHomeEvent event) throws BotHomeError, UserError {
        User sender = event.getSender();

        List<String> inmates = event.getServiceHome().clearRole(prisonRole);

        HomeEditor homeEditor = event.getHomeEditor();
        homeEditor.removeStorageVariables(variableName);

        if (inmates.isEmpty()) {
            return;
        }

        event.say(String.format("%s broke %s out of %s!", sender.getName(), Strings.join(inmates),prisonRole));
    }
}
