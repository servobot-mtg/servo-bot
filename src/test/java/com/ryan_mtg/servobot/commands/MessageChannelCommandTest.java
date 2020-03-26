package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.commands.chat.MessageChannelCommand;
import com.ryan_mtg.servobot.commands.hierarchy.Command;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.model.Channel;
import com.ryan_mtg.servobot.model.Home;
import org.junit.Test;

import static com.ryan_mtg.servobot.model.ObjectMother.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MessageChannelCommandTest {
    private static final int ID = 1;
    private static final CommandSettings COMMAND_SETTINGS =
            new CommandSettings(Command.DEFAULT_FLAGS, Permission.MOD, null);
    private static final int SERVICE_TYPE = 3;
    private static final String CHANNEL_NAME = "channel_name";
    private static final String MESSAGE = "message";

    @Test
    public void testPerform() throws BotErrorException {
        MessageChannelCommand command =
                new MessageChannelCommand(ID, COMMAND_SETTINGS, SERVICE_TYPE, CHANNEL_NAME, MESSAGE);

        Home home = mockHome();
        Channel channel = mockChannel();
        when(home.getChannel(CHANNEL_NAME, SERVICE_TYPE)).thenReturn(channel);

        command.perform(mockHomeEvent(home));

        verify(channel).say(MESSAGE);
    }
}