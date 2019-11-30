package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.model.Book;
import com.ryan_mtg.servobot.model.Channel;
import com.ryan_mtg.servobot.model.Message;
import org.junit.Test;

import static com.ryan_mtg.servobot.model.ObjectMother.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FactsCommandTest {
    private static final int ID = 1;
    private static final boolean SECURE = true;
    private static final Permission PERMISSION = Permission.MOD;
    private static final String ARGUMENTS = "argument other_argument";
    private static final String LINE = "line";

    @Test
    public void testPerform() {
        Book book = mock(Book.class);
        when(book.getRandomLine()).thenReturn(LINE);

        FactsCommand command = new FactsCommand(ID, SECURE, PERMISSION, "TestFacts", book);

        Channel channel = mockChannel();
        Message message = mockMessage(channel);

        command.perform(message, ARGUMENTS);

        verify(channel).say(LINE);
    }
}