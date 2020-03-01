package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.model.Channel;
import com.ryan_mtg.servobot.model.Home;
import org.junit.Test;

import static com.ryan_mtg.servobot.model.ObjectMother.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MessageChannelCommandTest {
    private static final int ID = 1;
    private static final int SECURE = 1;
    private static final Permission PERMISSION = Permission.MOD;
    private static final int SERVICE_TYPE = 3;
    private static final String CHANNEL_NAME = "channel_name";
    private static final String MESSAGE = "message";

    @Test
    public void testPerform() throws BotErrorException {
        MessageChannelCommand command =
                new MessageChannelCommand(ID, SECURE, PERMISSION, SERVICE_TYPE, CHANNEL_NAME, MESSAGE);

        Home home = mockHome();
        Channel channel = mockChannel();
        when(home.getChannel(CHANNEL_NAME, SERVICE_TYPE)).thenReturn(channel);

        command.perform(mockHomeEvent(home));

        verify(channel).say(MESSAGE);
    }
}