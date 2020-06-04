package com.ryan_mtg.servobot.commands.hierarchy;

import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.CommandInvokedEvent;

public abstract class InvokedCommand extends Command {
    public InvokedCommand(final int id, final CommandSettings commandSettings) {
        super(id, commandSettings);
    }

    abstract public void perform(CommandInvokedEvent event) throws BotErrorException;
}
