package com.ryan_mtg.servobot.commands.roles;

import com.ryan_mtg.servobot.commands.CommandType;
import com.ryan_mtg.servobot.commands.CommandVisitor;
import com.ryan_mtg.servobot.commands.hierarchy.CommandSettings;
import com.ryan_mtg.servobot.commands.hierarchy.UserHomedCommand;
import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.events.UserHomeEvent;
import com.ryan_mtg.servobot.utility.Validation;
import lombok.Getter;

public class SetRoleCommand extends UserHomedCommand {
    public static final CommandType TYPE = CommandType.SET_ROLE_COMMAND_TYPE;

    @Getter
    private String role;

    public SetRoleCommand(final int id, final CommandSettings commandSettings, final String role) throws UserError {
        super(id, commandSettings);
        this.role = role;

        Validation.validateStringLength(role, Validation.MAX_ROLE_LENGTH, "Role");
    }

    @Override
    public void perform(final UserHomeEvent userHomeEvent) throws UserError {
        userHomeEvent.getServiceHome().setRole(userHomeEvent.getUser(), role);
    }

    @Override
    public CommandType getType() {
        return TYPE;
    }

    @Override
    public void acceptVisitor(final CommandVisitor commandVisitor) {
        commandVisitor.visitSetRoleCommand(this);
    }
}
