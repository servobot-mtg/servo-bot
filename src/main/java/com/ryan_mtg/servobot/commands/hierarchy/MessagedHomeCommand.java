package com.ryan_mtg.servobot.commands.hierarchy;

import com.ryan_mtg.servobot.error.BotHomeError;
import com.ryan_mtg.servobot.error.SystemError;
import com.ryan_mtg.servobot.events.MessageHomeEvent;

public abstract class MessagedHomeCommand extends Command {
    public MessagedHomeCommand(final int id, final CommandSettings commandSettings) {
        super(id, commandSettings);
    }

    abstract public void perform(MessageHomeEvent event) throws BotHomeError, SystemError;
}
