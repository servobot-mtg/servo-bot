package com.ryan_mtg.servobot.commands.hierarchy;

import com.ryan_mtg.servobot.events.UserEvent;

public abstract class UserCommand extends Command {
    public UserCommand(final int id, final CommandSettings commandSettings) {
        super(id, commandSettings);
    }

    public abstract void perform(UserEvent userEvent);
}
