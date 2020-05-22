package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.commands.hierarchy.Command;
import com.ryan_mtg.servobot.commands.hierarchy.CommandSettings;
import com.ryan_mtg.servobot.commands.hierarchy.RateLimit;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.MessageSentEvent;
import com.ryan_mtg.servobot.model.Channel;
import com.ryan_mtg.servobot.model.Home;
import com.ryan_mtg.servobot.model.Message;
import com.ryan_mtg.servobot.model.User;
import org.junit.Test;

import static com.ryan_mtg.servobot.model.ObjectMother.mockChannel;
import static com.ryan_mtg.servobot.model.ObjectMother.mockHome;
import static com.ryan_mtg.servobot.model.ObjectMother.mockMessage;
import static com.ryan_mtg.servobot.model.ObjectMother.mockMessageSentEvent;
import static com.ryan_mtg.servobot.model.ObjectMother.mockUser;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TierCommandTest {
    private static final int ID = 1;
    private static final CommandSettings COMMAND_SETTINGS =
        new CommandSettings(Command.DEFAULT_FLAGS, Permission.MOD, new RateLimit());
    private static final String USER_NAME = "username";
    private static final String ROLE = "role";
    private static final int SERVICE_TYPE = 5;
    private static final String ARGUMENTS = "argument other_argument";

    @Test
    public void testPerform() throws BotErrorException {
        TierCommand command = new TierCommand(ID, COMMAND_SETTINGS);

        Home home = mockHome();
        Channel channel = mockChannel();
        User user = mockUser(USER_NAME);
        Message message = mockMessage(SERVICE_TYPE);
        MessageSentEvent event = mockMessageSentEvent(home, channel, user, message);

        when(home.getRole(user, SERVICE_TYPE)).thenReturn(ROLE);

        command.perform(event, ARGUMENTS);

        verify(channel).say(String.format("Hello, %s, your friendship tier is %s.", USER_NAME, ROLE));
    }
}