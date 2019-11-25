package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.model.Channel;
import com.ryan_mtg.servobot.model.Message;
import org.junit.Test;

import java.util.Random;

import static com.ryan_mtg.servobot.model.ObjectMother.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FactsCommandTest {
    private static final int ID = 1;
    private static final boolean SECURE = true;
    private static final Permission PERMISSION = Permission.MOD;
    private static final String ARGUMENTS = "argument other_argument";

    @Test
    public void testPerform() {
        Random random = mock(Random.class);
        when(random.nextInt(2)).thenReturn(0);

        FactsCommand command = new FactsCommand(ID, SECURE, PERMISSION, "TestFacts", random);

        Channel channel = mockChannel();
        Message message = mockMessage(channel);

        command.perform(message, ARGUMENTS);

        verify(channel).say("Fact 1");
    }

    @Test
    public void testPerformWithDifferentRandomResult() {
        Random random = mock(Random.class);
        when(random.nextInt(2)).thenReturn(1);

        FactsCommand command = new FactsCommand(ID, SECURE, PERMISSION, "TestFacts", random);

        Channel channel = mockChannel();
        Message message = mockMessage(channel);

        command.perform(message, ARGUMENTS);

        verify(channel).say("Fact 2");
    }
}