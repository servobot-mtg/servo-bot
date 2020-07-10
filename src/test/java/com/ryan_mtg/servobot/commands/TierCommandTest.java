package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.commands.hierarchy.Command;
import com.ryan_mtg.servobot.commands.hierarchy.CommandSettings;
import com.ryan_mtg.servobot.commands.hierarchy.RateLimit;
import com.ryan_mtg.servobot.error.BotHomeError;
import com.ryan_mtg.servobot.events.CommandInvokedHomeEvent;
import com.ryan_mtg.servobot.model.ServiceHome;
import com.ryan_mtg.servobot.model.User;
import org.junit.Test;

import static com.ryan_mtg.servobot.model.ObjectMother.mockCommandInvokedHomeEvent;
import static com.ryan_mtg.servobot.model.ObjectMother.mockServiceHome;
import static com.ryan_mtg.servobot.model.ObjectMother.mockUser;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TierCommandTest {
    private static final int ID = 1;
    private static final CommandSettings COMMAND_SETTINGS =
        new CommandSettings(Command.DEFAULT_FLAGS, Permission.MOD, new RateLimit());
    private static final String USER_NAME = "username";
    private static final String ROLE = "role";
    private static final String ARGUMENTS = "argument other_argument";

    @Test
    public void testPerform() throws BotHomeError {
        TierCommand command = new TierCommand(ID, COMMAND_SETTINGS);

        ServiceHome serviceHome = mockServiceHome();
        User user = mockUser(USER_NAME);
        CommandInvokedHomeEvent event = mockCommandInvokedHomeEvent(serviceHome, user, ARGUMENTS);

        when(serviceHome.getRole(user)).thenReturn(ROLE);

        command.perform(event);

        verify(event).say(String.format("Hello, %s, your friendship tier is %s.", USER_NAME, ROLE));
    }
}