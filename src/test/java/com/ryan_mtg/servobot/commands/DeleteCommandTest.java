package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.MessageSentEvent;
import com.ryan_mtg.servobot.model.Channel;
import com.ryan_mtg.servobot.model.HomeEditor;
import org.junit.Test;

import static com.ryan_mtg.servobot.model.ObjectMother.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class DeleteCommandTest {
    private static final int ID = 1;
    private static final int SECURE = 1;
    private static final Permission PERMISSION = Permission.MOD;
    private static final String COMMAND_NAME = "command_name";

    @Test
    public void testPerform() throws BotErrorException {
        DeleteCommand command = new DeleteCommand(ID, SECURE, PERMISSION);

        HomeEditor homeEditor = mockHomeEditor();
        Channel channel = mockChannel();
        MessageSentEvent event = mockMessageSentEvent(homeEditor, channel);

        command.perform(event, String.format("!%s", COMMAND_NAME));

        verify(homeEditor).deleteCommand(COMMAND_NAME);
        verify(channel).say(String.format("Command %s deleted.", COMMAND_NAME));
    }

    @Test(expected = BotErrorException.class)
    public void testThrowsExceptionWhenCommandDoesNotStartWithExclamationMark() throws BotErrorException {
        DeleteCommand command = new DeleteCommand(ID, SECURE, PERMISSION);

        Channel channel = mockChannel();
        MessageSentEvent event = mockMessageSentEvent(channel);

        try {
            command.perform(event, String.format("%s", COMMAND_NAME));
        } finally {
            verify(channel, never()).say(anyString());
        }
    }

    @Test(expected = BotErrorException.class)
    public void testThrowsExceptionWhenCommandNameIsEmpty() throws BotErrorException {
        DeleteCommand command = new DeleteCommand(ID, SECURE, PERMISSION);

        Channel channel = mockChannel();
        MessageSentEvent event = mockMessageSentEvent(channel);

        try {
            command.perform(event, String.format("!%s", ""));
        } finally {
            verify(channel, never()).say(anyString());
        }
    }

    @Test(expected = BotErrorException.class)
    public void testThrowsExceptionWhenPassedNoArguments() throws BotErrorException {
        DeleteCommand command = new DeleteCommand(ID, SECURE, PERMISSION);

        Channel channel = mockChannel();
        MessageSentEvent event = mockMessageSentEvent(channel);

        try {
            command.perform(event, "");
        } finally {
            verify(channel, never()).say(anyString());
        }
    }

    @Test(expected = BotErrorException.class)
    public void testDoesNotSayAnythingWhenHomeEditorThrows() throws BotErrorException {
        DeleteCommand command = new DeleteCommand(ID, SECURE, PERMISSION);

        HomeEditor homeEditor = mockHomeEditor();
        Channel channel = mockChannel();
        MessageSentEvent event = mockMessageSentEvent(homeEditor, channel);

        doThrow(BotErrorException.class).when(homeEditor).deleteCommand(COMMAND_NAME);

        try {
            command.perform(event, String.format("!%s", COMMAND_NAME));
        } finally {
            verify(channel, never()).say(anyString());
        }
    }
}