package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.commands.chat.TextCommand;
import com.ryan_mtg.servobot.commands.hierarchy.Command;
import com.ryan_mtg.servobot.commands.hierarchy.CommandSettings;
import com.ryan_mtg.servobot.commands.hierarchy.RateLimit;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.CommandInvokedEvent;
import com.ryan_mtg.servobot.model.Channel;
import com.ryan_mtg.servobot.model.scope.SimpleSymbolTable;
import com.ryan_mtg.servobot.model.scope.Scope;
import com.ryan_mtg.servobot.model.storage.IntegerStorageValue;
import com.ryan_mtg.servobot.model.storage.StorageValue;
import org.junit.Test;

import static com.ryan_mtg.servobot.model.ObjectMother.mockChannel;
import static com.ryan_mtg.servobot.model.ObjectMother.mockCommandInvokedEvent;
import static com.ryan_mtg.servobot.model.ObjectMother.mockUser;
import static org.mockito.Mockito.verify;

public class TextCommandTest {
    private static final int ID = 1;
    private static final CommandSettings COMMAND_SETTINGS =
            new CommandSettings(Command.DEFAULT_FLAGS, Permission.MOD, new RateLimit());
    private static final String TEXT = "text";
    private static final String ARGUMENTS = "argument other_argument";

    @Test
    public void testPerform() throws BotErrorException {
        TextCommand command = new TextCommand(ID, COMMAND_SETTINGS, TEXT);

        Channel channel = mockChannel();
        CommandInvokedEvent event = mockCommandInvokedEvent(channel, ARGUMENTS);

        command.perform(event);

        verify(channel).say(TEXT);
    }

    @Test
    public void testPerformSubstitutesUserName() throws BotErrorException {
        TextCommand command = new TextCommand(ID, COMMAND_SETTINGS, "Hello, %sender%!");

        Channel channel = mockChannel();
        CommandInvokedEvent event = mockCommandInvokedEvent(channel, mockUser("name"), ARGUMENTS);

        command.perform(event);

        verify(channel).say("Hello, name!");
    }

    @Test
    public void testPerformSubstitutesVariable() throws BotErrorException {
        TextCommand command = new TextCommand(ID, COMMAND_SETTINGS, "Value: %value%");

        Channel channel = mockChannel();

        SimpleSymbolTable symbolTable = new SimpleSymbolTable();
        Scope scope = new Scope(null, symbolTable);
        symbolTable.addValue("value", new IntegerStorageValue(
                StorageValue.UNREGISTERED_ID, StorageValue.GLOBAL_USER, "value", 1));
        CommandInvokedEvent event = mockCommandInvokedEvent(scope, channel, ARGUMENTS);

        command.perform(event);

        verify(channel).say("Value: 1");
    }
}