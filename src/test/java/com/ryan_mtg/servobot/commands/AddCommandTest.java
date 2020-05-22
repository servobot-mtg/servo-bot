package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.commands.chat.AddCommand;
import com.ryan_mtg.servobot.commands.hierarchy.Command;
import com.ryan_mtg.servobot.commands.hierarchy.CommandSettings;
import com.ryan_mtg.servobot.commands.hierarchy.MessageCommand;
import com.ryan_mtg.servobot.commands.hierarchy.RateLimit;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.MessageSentEvent;
import com.ryan_mtg.servobot.model.Channel;
import com.ryan_mtg.servobot.model.HomeEditor;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import static com.ryan_mtg.servobot.model.ObjectMother.isACommand;
import static com.ryan_mtg.servobot.model.ObjectMother.isATextCommand;
import static com.ryan_mtg.servobot.model.ObjectMother.mockChannel;
import static com.ryan_mtg.servobot.model.ObjectMother.mockMessageSentEvent;
import static com.ryan_mtg.servobot.model.ObjectMother.mockHomeEditor;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
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
    public void testPerform() throws BotErrorException {
        AddCommand command = new AddCommand(ID, COMMAND_SETTINGS);

        HomeEditor homeEditor = mockHomeEditor();
        Channel channel = mockChannel();
        MessageSentEvent event = mockMessageSentEvent(homeEditor, channel);
        when(homeEditor.addCommand(eq(COMMAND_NAME), Mockito.any(MessageCommand.class))).thenReturn(true);

        command.perform(event, String.format("!%s %s", COMMAND_NAME, ARGUMENTS));

        verify(channel).say(String.format("Command %s added.", COMMAND_NAME));
        ArgumentCaptor<MessageCommand> commandCaptor = ArgumentCaptor.forClass(MessageCommand.class);
        verify(homeEditor).addCommand(eq(COMMAND_NAME), commandCaptor.capture());

        MessageCommand capturedCommand = commandCaptor.getValue();

        assertThat(capturedCommand, isACommand(false, Permission.ANYONE));
        assertThat(capturedCommand, isATextCommand(ARGUMENTS));
    }

    @Test
    public void testPerformWhenCommandAlreadyExists() throws BotErrorException {
        AddCommand command = new AddCommand(ID, COMMAND_SETTINGS);

        HomeEditor homeEditor = mockHomeEditor();
        Channel channel = mockChannel();
        MessageSentEvent event = mockMessageSentEvent(homeEditor, channel);
        when(homeEditor.addCommand(eq(COMMAND_NAME), Mockito.any(MessageCommand.class))).thenReturn(false);

        command.perform(event, String.format("!%s %s", COMMAND_NAME, ARGUMENTS));

        verify(channel).say(String.format("Command %s modified.", COMMAND_NAME));
        ArgumentCaptor<MessageCommand> commandCaptor = ArgumentCaptor.forClass(MessageCommand.class);
        verify(homeEditor).addCommand(eq(COMMAND_NAME), commandCaptor.capture());

        MessageCommand capturedCommand = commandCaptor.getValue();

        assertThat(capturedCommand, isACommand(false, Permission.ANYONE));
        assertThat(capturedCommand, isATextCommand(ARGUMENTS));
    }

    @Test(expected = BotErrorException.class)
    public void testThrowsExceptionWhenCommandDoesNotStartWithExclamationMark() throws BotErrorException {
        AddCommand command = new AddCommand(ID, COMMAND_SETTINGS);

        Channel channel = mockChannel();
        MessageSentEvent event = mockMessageSentEvent(channel);

        try {
            command.perform(event, String.format("%s %s", COMMAND_NAME, ARGUMENTS));
        } finally {
            verify(channel, never()).say(anyString());
        }
    }

    @Test(expected = BotErrorException.class)
    public void testThrowsExceptionWhenCommandNameIsEmpty() throws BotErrorException {
        AddCommand command = new AddCommand(ID, COMMAND_SETTINGS);

        Channel channel = mockChannel();
        MessageSentEvent event = mockMessageSentEvent(channel);

        try {
            command.perform(event, String.format("!%s %s", "", ARGUMENTS));
        } finally {
            verify(channel, never()).say(anyString());
        }
    }

    @Test(expected = BotErrorException.class)
    public void testThrowsExceptionWhenPassedNoArguments() throws BotErrorException {
        AddCommand command = new AddCommand(ID, COMMAND_SETTINGS);

        Channel channel = mockChannel();
        MessageSentEvent event = mockMessageSentEvent(channel);

        try {
            command.perform(event, String.format("!%s%s", COMMAND_NAME, ""));
        } finally {
            verify(channel, never()).say(anyString());
        }
    }

    @Test(expected = BotErrorException.class)
    public void testThrowsExceptionWhenPassedWhitespaceAsArguments() throws BotErrorException {
        AddCommand command = new AddCommand(ID, COMMAND_SETTINGS);

        Channel channel = mockChannel();
        MessageSentEvent event = mockMessageSentEvent(channel);

        try {
            command.perform(event, String.format("!%s %s", COMMAND_NAME, " "));
        } finally {
            verify(channel, never()).say(anyString());
        }
    }
}
