package com.ryan_mtg.servobot.commands.hierarchy;

import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.CommandInvokedHomeEvent;

public abstract class InvokedHomedCommand extends Command {
    public InvokedHomedCommand(final int id, final CommandSettings commandSettings) {
        super(id, commandSettings);
    }

    abstract public void perform(CommandInvokedHomeEvent event) throws BotErrorException;
}
