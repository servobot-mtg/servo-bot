package com.ryan_mtg.servobot.commands.hierarchy;

import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.HomeEvent;

public abstract class HomeCommand extends Command {
    public HomeCommand(final int id, final CommandSettings commandSettings) {
        super(id, commandSettings);
    }

    public abstract void perform(HomeEvent homeEvent) throws BotErrorException;
}
