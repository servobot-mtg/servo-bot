package com.ryan_mtg.servobot.commands.hierarchy;

import com.ryan_mtg.servobot.error.BotHomeError;
import com.ryan_mtg.servobot.events.UserHomeEvent;

public abstract class UserHomedCommand extends Command {
    public UserHomedCommand(final int id, final CommandSettings commandSettings) {
        super(id, commandSettings);
    }

    public abstract void perform(UserHomeEvent userHomeEvent) throws BotHomeError;
}
