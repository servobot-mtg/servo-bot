package com.ryan_mtg.servobot.commands.hierarchy;

import com.ryan_mtg.servobot.error.BotHomeError;
import com.ryan_mtg.servobot.error.SystemError;
import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.events.HomeEvent;

public abstract class HomeCommand extends Command {
    public HomeCommand(final int id, final CommandSettings commandSettings) {
        super(id, commandSettings);
    }

    public abstract void perform(HomeEvent homeEvent) throws BotHomeError, SystemError, UserError;
}
