package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.model.Channel;
import com.ryan_mtg.servobot.model.Message;
import org.junit.Test;

import static com.ryan_mtg.servobot.model.ObjectMother.*;
import static org.mockito.Mockito.verify;

public class TextCommandTest {
    private static final int ID = 1;
    private static final boolean SECURE = true;
    private static final String TEXT = "text";
    private static final String ARGUMENTS = "argument other_argument";

    @Test
    public void testPerform() {
        TextCommand command = new TextCommand(ID, SECURE, TEXT);

        Channel channel = mockChannel();
        Message message = mockMessage(channel);

        command.perform(message, ARGUMENTS);

        verify(channel).say(TEXT);
    }

    @Test
    public void testPerformSubstitutesUserName() {
        TextCommand command = new TextCommand(ID, SECURE, "Hello, %user%!");

        Channel channel = mockChannel();
        Message message = mockMessage(channel, mockUser("name"));

        command.perform(message, ARGUMENTS);

        verify(channel).say("Hello, name!");
    }
}