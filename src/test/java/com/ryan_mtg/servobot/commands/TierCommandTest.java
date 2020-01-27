package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.events.MessageSentEvent;
import com.ryan_mtg.servobot.model.Channel;
import com.ryan_mtg.servobot.model.Home;
import com.ryan_mtg.servobot.model.Message;
import com.ryan_mtg.servobot.model.User;
import org.junit.Test;

import static com.ryan_mtg.servobot.model.ObjectMother.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TierCommandTest {
    private static final int ID = 1;
    private static final int FLAGS = 1;
    private static final Permission PERMISSION = Permission.MOD;
    private static final String USER_NAME = "username";
    private static final String ROLE = "role";
    private static final int SERVICE_TYPE = 5;
    private static final String ARGUMENTS = "argument other_argument";

    @Test
    public void testPerform() {
        TierCommand command = new TierCommand(ID, FLAGS, PERMISSION);

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