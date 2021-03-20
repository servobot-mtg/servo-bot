package com.ryan_mtg.servobot.commands.roles;

import com.ryan_mtg.servobot.commands.CommandType;
import com.ryan_mtg.servobot.commands.CommandVisitor;
import com.ryan_mtg.servobot.commands.hierarchy.CommandSettings;
import com.ryan_mtg.servobot.commands.hierarchy.UserHomedCommand;
import com.ryan_mtg.servobot.error.BotHomeError;
import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.events.UserHomeEvent;
import lombok.Getter;

public class SetRoleCommand extends UserHomedCommand {
    public static final CommandType TYPE = CommandType.SET_ROLE_COMMAND_TYPE;

    @Getter
    private long roleId;

    public SetRoleCommand(final int id, final CommandSettings commandSettings, final long roleId) throws UserError {
        super(id, commandSettings);
        this.roleId = roleId;
    }

    @Override
    public void perform(final UserHomeEvent userHomeEvent) throws BotHomeError {
        userHomeEvent.getServiceHome().setRole(userHomeEvent.getUser(), roleId);
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