package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.MessageSentEvent;
import com.ryan_mtg.servobot.model.Channel;
import com.ryan_mtg.servobot.model.HomeEditor;
import com.ryan_mtg.servobot.model.storage.IntegerStorageValue;
import com.ryan_mtg.servobot.model.storage.StorageValue;
import org.junit.Test;

import static com.ryan_mtg.servobot.model.ObjectMother.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TextCommandTest {
    private static final int ID = 1;
    private static final boolean SECURE = true;
    private static final Permission PERMISSION = Permission.MOD;
    private static final String TEXT = "text";
    private static final String ARGUMENTS = "argument other_argument";

    @Test
    public void testPerform() throws BotErrorException {
        TextCommand command = new TextCommand(ID, SECURE, PERMISSION, TEXT);

        Channel channel = mockChannel();
        MessageSentEvent event = mockMessageSentEvent(channel);

        command.perform(event, ARGUMENTS);

        verify(channel).say(TEXT);
    }

    @Test
    public void testPerformSubstitutesUserName() throws BotErrorException {
        TextCommand command = new TextCommand(ID, SECURE, PERMISSION, "Hello, %user%!");

        Channel channel = mockChannel();
        MessageSentEvent event = mockMessageSentEvent(channel, mockUser("name"));

        command.perform(event, ARGUMENTS);

        verify(channel).say("Hello, name!");
    }

    @Test
    public void testPerformSubstitutesVariable() throws BotErrorException {
        TextCommand command = new TextCommand(ID, SECURE, PERMISSION, "Value: %show value%");

        Channel channel = mockChannel();
        HomeEditor homeEditor = mockHomeEditor();
        MessageSentEvent event = mockMessageSentEvent(homeEditor, channel);
        StorageValue value = new IntegerStorageValue( StorageValue.UNREGISTERED_ID, "value", 1);
        when(homeEditor.getStorageValue("value")).thenReturn(value);

        command.perform(event, ARGUMENTS);

        verify(channel).say("Value: 1");
    }
}