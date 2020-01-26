package com.ryan_mtg.servobot.commands;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class EvaluateExpressionCommandTest {
    @Test
    public void testGabyEasterEgg() {
        assertTrue(EvaluateExpressionCommand.gabyEasterEggPattern.matcher("2+2").matches());
    }
}