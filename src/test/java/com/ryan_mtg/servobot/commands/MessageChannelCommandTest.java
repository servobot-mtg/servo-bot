package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.commands.chat.MessageChannelCommand;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.HomeEvent;
import com.ryan_mtg.servobot.model.Channel;
import com.ryan_mtg.servobot.model.Home;
import com.ryan_mtg.servobot.model.ServiceHome;
import org.junit.Before;
import org.junit.Test;

import static com.ryan_mtg.servobot.model.ObjectMother.mockChannel;
import static com.ryan_mtg.servobot.model.ObjectMother.mockHome;
import static com.ryan_mtg.servobot.model.ObjectMother.mockHomeEvent;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MessageChannelCommandTest {
    private static final int ID = 1;
    private static final int SECURE = 1;
    private static final Permission PERMISSION = Permission.MOD;
    private static final int SERVICE_TYPE = 3;
    private static final int OTHER_SERVICE_TYPE = 2;
    private static final String CHANNEL_NAME = "channel_name";
    private static final String MESSAGE = "message";

    private MessageChannelCommand command;
    private Home home;
    private Channel channel;

    @Before
    public void setUp() throws BotErrorException {
        command = new MessageChannelCommand(ID, SECURE, PERMISSION, SERVICE_TYPE, CHANNEL_NAME, MESSAGE);

        home = mockHome();
        channel = mockChannel();
    }

    @Test
    public void testPerformOnSameService() throws BotErrorException {
        when(home.getChannel(CHANNEL_NAME, SERVICE_TYPE)).thenReturn(channel);

        command.perform(mockHomeEvent(home, SERVICE_TYPE));

        verify(channel).say(MESSAGE);
    }

    @Test
    public void testPerformOnDifferentService() throws BotErrorException {
        ServiceHome serviceHome = mock(ServiceHome.class);
        when(serviceHome.getChannel(CHANNEL_NAME)).thenReturn(channel);

        HomeEvent homeEvent = mockHomeEvent(home, OTHER_SERVICE_TYPE);
        when(homeEvent.getServiceHome(SERVICE_TYPE)).thenReturn(serviceHome);

        command.perform(homeEvent);

        verify(channel).say(MESSAGE);
    }
}