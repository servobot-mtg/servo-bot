package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.commands.chat.AddCommand;
import com.ryan_mtg.servobot.commands.hierarchy.Command;
import com.ryan_mtg.servobot.commands.hierarchy.CommandSettings;
import com.ryan_mtg.servobot.commands.hierarchy.InvokedCommand;
import com.ryan_mtg.servobot.commands.hierarchy.RateLimit;
import com.ryan_mtg.servobot.error.BotHomeError;
import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.events.CommandInvokedEvent;
import com.ryan_mtg.servobot.model.editors.CommandTableEditor;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import static com.ryan_mtg.servobot.model.ObjectMother.isACommand;
import static com.ryan_mtg.servobot.model.ObjectMother.isATextCommand;
import static com.ryan_mtg.servobot.model.ObjectMother.mockCommandInvokedEvent;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AddCommandTest {
    private static final int ID = 1;
    private static final CommandSettings COMMAND_SETTINGS =
            new CommandSettings(Command.DEFAULT_FLAGS, Permission.MOD, new RateLimit());
    private static final String COMMAND_NAME = "command_name";
    private static final String ARGUMENTS = "argument other_argument";

    @Test
    public void testPerform() throws BotHomeError, UserError {
        AddCommand command = new AddCommand(ID, COMMAND_SETTINGS);

        CommandTableEditor commandTableEditor = mock(CommandTableEditor.class);
        String commmandLine = String.format("!%s %s", COMMAND_NAME, ARGUMENTS);
        CommandInvokedEvent event = mockCommandInvokedEvent(commandTableEditor, commmandLine);
        when(commandTableEditor.addCommand(eq(COMMAND_NAME), Mockito.any(InvokedCommand.class))).thenReturn(true);

        command.perform(event);

        verify(event).say(String.format("Command %s added.", COMMAND_NAME));
        ArgumentCaptor<InvokedCommand> commandCaptor = ArgumentCaptor.forClass(InvokedCommand.class);
        verify(commandTableEditor).addCommand(eq(COMMAND_NAME), commandCaptor.capture());

        InvokedCommand capturedCommand = commandCaptor.getValue();

        assertThat(capturedCommand, isACommand(false, Permission.ANYONE));
        assertThat(capturedCommand, isATextCommand(ARGUMENTS));
    }

    @Test
    public void testPerformWhenCommandAlreadyExists() throws BotHomeError, UserError {
        AddCommand command = new AddCommand(ID, COMMAND_SETTINGS);

        CommandTableEditor commandTableEditor = mock(CommandTableEditor.class);
        String commandLine = String.format("!%s %s", COMMAND_NAME, ARGUMENTS);
        CommandInvokedEvent event = mockCommandInvokedEvent(commandTableEditor, commandLine);
        when(commandTableEditor.addCommand(eq(COMMAND_NAME), Mockito.any(InvokedCommand.class))).thenReturn(false);

        command.perform(event);

        verify(event).say(String.format("Command %s modified.", COMMAND_NAME));
        ArgumentCaptor<InvokedCommand> commandCaptor = ArgumentCaptor.forClass(InvokedCommand.class);
        verify(commandTableEditor).addCommand(eq(COMMAND_NAME), commandCaptor.capture());

        InvokedCommand capturedCommand = commandCaptor.getValue();

        assertThat(capturedCommand, isACommand(false, Permission.ANYONE));
        assertThat(capturedCommand, isATextCommand(ARGUMENTS));
    }

    @Test(expected = UserError.class)
    public void testThrowsExceptionWhenCommandDoesNotStartWithExclamationMark() throws BotHomeError, UserError {
        AddCommand command = new AddCommand(ID, COMMAND_SETTINGS);

        String commandLine = String.format("%s %s", COMMAND_NAME, ARGUMENTS);
        CommandInvokedEvent event = mockCommandInvokedEvent(commandLine);

        try {
            command.perform(event);
        } finally {
            verify(event, never()).say(anyString());
        }
    }

    @Test(expected = UserError.class)
    public void testThrowsExceptionWhenCommandNameIsEmpty() throws BotHomeError, UserError {
        AddCommand command = new AddCommand(ID, COMMAND_SETTINGS);

        String commandLine = String.format("!%s %s", "", ARGUMENTS);
        CommandInvokedEvent event = mockCommandInvokedEvent(commandLine);

        try {
            command.perform(event);
        } finally {
            verify(event, never()).say(anyString());
        }
    }

    @Test(expected = UserError.class)
    public void testThrowsExceptionWhenPassedNoArguments() throws BotHomeError, UserError {
        AddCommand command = new AddCommand(ID, COMMAND_SETTINGS);

        String commandLine = String.format("!%s%s", COMMAND_NAME, "");
        CommandInvokedEvent event = mockCommandInvokedEvent(commandLine);

        try {
            command.perform(event);
        } finally {
            verify(event, never()).say(anyString());
        }
    }

    @Test(expected = UserError.class)
    public void testThrowsExceptionWhenPassedWhitespaceAsArguments() throws BotHomeError, UserError {
        AddCommand command = new AddCommand(ID, COMMAND_SETTINGS);

        String commandLine = String.format("!%s %s", COMMAND_NAME, " ");
        CommandInvokedEvent event = mockCommandInvokedEvent(commandLine);

        try {
            command.perform(event);
        } finally {
            verify(event, never()).say(anyString());
        }
    }
}
