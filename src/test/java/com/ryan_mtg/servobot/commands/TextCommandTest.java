package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.events.MessageSentEvent;
import com.ryan_mtg.servobot.model.Channel;
import org.junit.Test;

import static com.ryan_mtg.servobot.model.ObjectMother.*;
import static org.mockito.Mockito.verify;

public class TextCommandTest {
    private static final int ID = 1;
    private static final boolean SECURE = true;
    private static final Permission PERMISSION = Permission.MOD;
    private static final String TEXT = "text";
    private static final String ARGUMENTS = "argument other_argument";

    @Test
    public void testPerform() {
        TextCommand command = new TextCommand(ID, SECURE, PERMISSION, TEXT);

        Channel channel = mockChannel();
        MessageSentEvent event = mockMessageSentEvent(channel);

        command.perform(event, ARGUMENTS);

        verify(channel).say(TEXT);
    }

    @Test
    public void testPerformSubstitutesUserName() {
        TextCommand command = new TextCommand(ID, SECURE, PERMISSION, "Hello, %user%!");

        Channel channel = mockChannel();
        MessageSentEvent event = mockMessageSentEvent(channel, mockUser("name"));

        command.perform(event, ARGUMENTS);

        verify(channel).say("Hello, name!");
    }
}