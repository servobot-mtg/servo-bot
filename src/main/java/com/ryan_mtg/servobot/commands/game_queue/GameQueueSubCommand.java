package com.ryan_mtg.servobot.commands.game_queue;

import com.ryan_mtg.servobot.error.BotHomeError;
import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.events.CommandInvokedHomeEvent;
import com.ryan_mtg.servobot.utility.CommandParser;

public interface GameQueueSubCommand {
    void execute(final CommandInvokedHomeEvent event, final CommandParser.ParseResult parsedCommand)
            throws BotHomeError, UserError;
}
