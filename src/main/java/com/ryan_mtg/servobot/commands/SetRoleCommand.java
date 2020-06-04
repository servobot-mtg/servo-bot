package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.commands.hierarchy.CommandSettings;
import com.ryan_mtg.servobot.commands.hierarchy.UserHomedCommand;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.UserHomeEvent;
import com.ryan_mtg.servobot.utility.Validation;
import lombok.Getter;

public class SetRoleCommand extends UserHomedCommand {
    public static final CommandType TYPE = CommandType.SET_ROLE_COMMAND_TYPE;

    @Getter
    private String role;

    public SetRoleCommand(final int id, final CommandSettings commandSettings, final String role)
            throws BotErrorException {
        super(id, commandSettings);
        this.role = role;

        Validation.validateStringLength(role, Validation.MAX_ROLE_LENGTH, "Role");
    }

    @Override
    public void perform(final UserHomeEvent userHomeEvent) throws BotErrorException {
        userHomeEvent.getHome().setRole(userHomeEvent.getUser(), role);
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
