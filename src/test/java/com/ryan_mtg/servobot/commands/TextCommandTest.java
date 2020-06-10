package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.commands.chat.TextCommand;
import com.ryan_mtg.servobot.commands.hierarchy.Command;
import com.ryan_mtg.servobot.commands.hierarchy.CommandSettings;
import com.ryan_mtg.servobot.commands.hierarchy.RateLimit;
import com.ryan_mtg.servobot.error.BotHomeError;
import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.events.CommandInvokedEvent;
import com.ryan_mtg.servobot.model.scope.SymbolTable;
import org.junit.Test;

import static com.ryan_mtg.servobot.model.ObjectMother.mockCommandInvokedEvent;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

public class TextCommandTest {
    private static final int ID = 1;
    private static final CommandSettings COMMAND_SETTINGS =
            new CommandSettings(Command.DEFAULT_FLAGS, Permission.MOD, new RateLimit());
    private static final String TEXT = "text";
    private static final String ARGUMENTS = "argument other_argument";

    @Test
    public void testPerform() throws BotHomeError, UserError {
        TextCommand command = new TextCommand(ID, COMMAND_SETTINGS, TEXT);
        CommandInvokedEvent event = mockCommandInvokedEvent(ARGUMENTS);

        command.perform(event);

        verify(event).say(any(SymbolTable.class), eq(TEXT));
    }
}