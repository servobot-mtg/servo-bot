package com.ryan_mtg.servobot.commands.hierarchy;

import com.ryan_mtg.servobot.error.BotError;
import com.ryan_mtg.servobot.error.BotHomeError;
import com.ryan_mtg.servobot.error.SystemError;
import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.events.CommandInvokedEvent;

public abstract class InvokedCommand extends Command {
    public InvokedCommand(final int id, final CommandSettings commandSettings) {
        super(id, commandSettings);
    }

    abstract public void perform(CommandInvokedEvent event) throws BotError, BotHomeError, SystemError, UserError;
}
