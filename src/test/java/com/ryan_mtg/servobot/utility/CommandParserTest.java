package com.ryan_mtg.servobot.utility;

import org.junit.Before;
import org.junit.Test;

import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertTrue;

public class CommandParserTest {
    private static final Pattern COMMAND_PATTERN = Pattern.compile("\\w+");
    private static final Pattern FLAG_PATTERN = Pattern.compile("-\\w+");

    private CommandParser commandParser;

    @Before
    public void setUp() {
        commandParser = new CommandParser(COMMAND_PATTERN, FLAG_PATTERN);
    }

    @Test
    public void testParseEmptyString() {
        CommandParser.ParseResult result = commandParser.parse("");
        assertEquals(CommandParser.ParseStatus.NO_COMMAND, result.getStatus());
    }

    @Test
    public void testParseCommand() {
        CommandParser.ParseResult result = commandParser.parse("command");

        assertEquals(CommandParser.ParseStatus.SUCCESS, result.getStatus());
        assertEquals("command", result.getCommand());
        assertNull(result.getInput());
        assertTrue(result.getFlags().isEmpty());
    }

    @Test
    public void testParseFlag() {
        CommandParser commandParser = new CommandParser(COMMAND_PATTERN, FLAG_PATTERN);

        CommandParser.ParseResult result = commandParser.parse("command -flag");

        assertEquals(CommandParser.ParseStatus.SUCCESS, result.getStatus());
        assertEquals("command", result.getCommand());
        assertNull(result.getInput());
        assertThat(result.getFlags(), contains("-flag"));
    }

    @Test
    public void testParseMultipleFlags() {
        CommandParser commandParser = new CommandParser(COMMAND_PATTERN, FLAG_PATTERN);

        CommandParser.ParseResult result = commandParser.parse("command -flag -flag2");
        assertEquals(CommandParser.ParseStatus.SUCCESS, result.getStatus());
        assertThat(result.getFlags(), contains("-flag", "-flag2"));
    }

    @Test
    public void testParseFlagWithMoreInput() {
        CommandParser commandParser = new CommandParser(COMMAND_PATTERN, FLAG_PATTERN);

        CommandParser.ParseResult result = commandParser.parse("command -flag input");

        assertEquals(CommandParser.ParseStatus.SUCCESS, result.getStatus());
        assertEquals("command", result.getCommand());
        assertEquals("input", result.getInput());
        assertThat(result.getFlags(), contains("-flag"));
    }
}