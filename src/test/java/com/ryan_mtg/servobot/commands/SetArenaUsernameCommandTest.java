package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.commands.hierarchy.Command;
import com.ryan_mtg.servobot.commands.hierarchy.CommandSettings;
import com.ryan_mtg.servobot.commands.hierarchy.RateLimit;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.CommandInvokedEvent;
import com.ryan_mtg.servobot.model.BotEditor;
import com.ryan_mtg.servobot.model.User;
import org.junit.Test;

import static com.ryan_mtg.servobot.model.ObjectMother.mockBotEditor;
import static com.ryan_mtg.servobot.model.ObjectMother.mockCommandInvokedEvent;
import static com.ryan_mtg.servobot.model.ObjectMother.mockUser;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class SetArenaUsernameCommandTest {
    private static final int ID = 1;
    private static final int USER_ID = 123;
    private static final CommandSettings COMMAND_SETTINGS =
            new CommandSettings(Command.DEFAULT_FLAGS, Permission.MOD, new RateLimit());
    private static final String ARENA_NAME = "name#12345";

    @Test
    public void testPerform() throws BotErrorException {
        SetArenaUsernameCommand command = new SetArenaUsernameCommand(ID, COMMAND_SETTINGS);

        BotEditor botEditor = mockBotEditor();
        User user = mockUser(USER_ID);
        CommandInvokedEvent event = mockCommandInvokedEvent(botEditor, user, ARENA_NAME);

        command.perform(event);

        verify(botEditor).setArenaUsername(USER_ID, ARENA_NAME);
        verify(event).say("Username added.");
    }

    @Test(expected = BotErrorException.class)
    public void testThrowsWhenUsernameIsTooLong() throws BotErrorException {
        SetArenaUsernameCommand command = new SetArenaUsernameCommand(ID, COMMAND_SETTINGS);

        BotEditor botEditor = mockBotEditor();
        CommandInvokedEvent event = mockCommandInvokedEvent(botEditor, "This is a really long username#12345");

        try {
            command.perform(event);
        } finally {
            verify(botEditor, never()).setArenaUsername(anyInt(), anyString());
            verify(event, never()).say(anyString());
        }
    }


    @Test(expected = BotErrorException.class)
    public void testThrowsWhenUsernameDoesNotHaveNumber() throws BotErrorException {
        SetArenaUsernameCommand command = new SetArenaUsernameCommand(ID, COMMAND_SETTINGS);

        BotEditor botEditor = mockBotEditor();
        CommandInvokedEvent event = mockCommandInvokedEvent(botEditor, "name");

        try {
            command.perform(event);
        } finally {
            verify(botEditor, never()).setArenaUsername(anyInt(), anyString());
            verify(event, never()).say(anyString());
        }
    }
}
