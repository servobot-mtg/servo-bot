package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.commands.chat.DeleteCommand;
import com.ryan_mtg.servobot.commands.hierarchy.Command;
import com.ryan_mtg.servobot.commands.hierarchy.CommandSettings;
import com.ryan_mtg.servobot.commands.hierarchy.RateLimit;
import com.ryan_mtg.servobot.error.BotHomeError;
import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.events.CommandInvokedEvent;
import com.ryan_mtg.servobot.model.User;
import com.ryan_mtg.servobot.model.editors.CommandTableEditor;
import org.junit.Test;

import static com.ryan_mtg.servobot.model.ObjectMother.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class DeleteCommandTest {
    private static final int ID = 1;
    private static final CommandSettings COMMAND_SETTINGS =
            new CommandSettings(Command.DEFAULT_FLAGS, Permission.MOD, new RateLimit());
    private static final String COMMAND_NAME = "command_name";

    @Test
    public void testPerform() throws BotHomeError, UserError {
        DeleteCommand command = new DeleteCommand(ID, COMMAND_SETTINGS);

        CommandTableEditor commandTableEditor = mock(CommandTableEditor.class);
        User sender = mockUser("name");
        String commandLine = String.format("!%s", COMMAND_NAME);
        CommandInvokedEvent event = mockCommandInvokedEvent(commandTableEditor, sender, commandLine);

        command.perform(event);

        verify(commandTableEditor).deleteCommand(sender, COMMAND_NAME);
        verify(event).say(String.format("Command %s deleted.", COMMAND_NAME));
    }

    @Test(expected = UserError.class)
    public void testThrowsExceptionWhenCommandDoesNotStartWithExclamationMark() throws BotHomeError, UserError {
        DeleteCommand command = new DeleteCommand(ID, COMMAND_SETTINGS);

        String commandLine = String.format("%s", COMMAND_NAME);
        CommandInvokedEvent event = mockCommandInvokedEvent(commandLine);

        try {
            command.perform(event);
        } finally {
            verify(event, never()).say(anyString());
        }
    }

    @Test(expected = UserError.class)
    public void testThrowsExceptionWhenCommandNameIsEmpty() throws BotHomeError, UserError {
        DeleteCommand command = new DeleteCommand(ID, COMMAND_SETTINGS);

        String commandLine = String.format("!%s", "");
        CommandInvokedEvent event = mockCommandInvokedEvent(commandLine);

        try {
            command.perform(event);
        } finally {
            verify(event, never()).say(anyString());
        }
    }

    @Test(expected = UserError.class)
    public void testThrowsExceptionWhenPassedNoArguments() throws BotHomeError, UserError {
        DeleteCommand command = new DeleteCommand(ID, COMMAND_SETTINGS);

        String commandLine = "";
        CommandInvokedEvent event = mockCommandInvokedEvent(commandLine);

        try {
            command.perform(event);
        } finally {
            verify(event, never()).say(anyString());
        }
    }

    @Test(expected = UserError.class)
    public void testDoesNotSayAnythingWhenHomeEditorThrows() throws BotHomeError, UserError {
        DeleteCommand command = new DeleteCommand(ID, COMMAND_SETTINGS);

        CommandTableEditor commandTableEditor = mock(CommandTableEditor.class);
        User sender = mockUser("name");
        String commandLine = String.format("!%s", COMMAND_NAME);
        CommandInvokedEvent event = mockCommandInvokedEvent(commandTableEditor, sender, commandLine);

        doThrow(UserError.class).when(commandTableEditor).deleteCommand(sender, COMMAND_NAME);

        try {
            command.perform(event);
        } finally {
            verify(event, never()).say(anyString());
        }
    }
}