package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.MessageSentEvent;
import com.ryan_mtg.servobot.model.Book;
import com.ryan_mtg.servobot.model.Channel;
import org.junit.Test;

import static com.ryan_mtg.servobot.model.ObjectMother.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FactsCommandTest {
    private static final int ID = 1;
    private static final CommandSettings COMMAND_SETTINGS =
            new CommandSettings(Command.DEFAULT_FLAGS, Permission.MOD, null);
    private static final String ARGUMENTS = "argument other_argument";
    private static final String LINE = "line";

    @Test
    public void testPerform() throws BotErrorException {
        Book book = mock(Book.class);
        when(book.getRandomLine()).thenReturn(LINE);

        FactsCommand command = new FactsCommand(ID, COMMAND_SETTINGS, book);

        Channel channel = mockChannel();
        MessageSentEvent event = mockMessageSentEvent(channel);

        command.perform(event, ARGUMENTS);

        verify(channel).say(LINE);
    }
}