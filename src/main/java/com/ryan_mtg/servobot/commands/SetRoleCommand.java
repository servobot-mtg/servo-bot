package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.model.Home;
import com.ryan_mtg.servobot.model.User;
import com.ryan_mtg.servobot.utility.Validation;

public class SetRoleCommand extends UserCommand {
    public static final int TYPE = 13;
    private String role;

    public SetRoleCommand(final  int id, final CommandSettings commandSettings, final String role)
            throws BotErrorException {
        super(id, commandSettings);
        this.role = role;

        Validation.validateStringLength(role, Validation.MAX_ROLE_LENGTH, "Role");
    }

    public String getRole() {
        return role;
    }

    @Override
    public void perform(final Home home, final User user) throws BotErrorException {
        home.setRole(user, role);
    }

    @Override
    public int getType() {
        return TYPE;
    }

    @Override
    public void acceptVisitor(final CommandVisitor commandVisitor) {
        commandVisitor.visitSetRoleCommand(this);
    }
}
