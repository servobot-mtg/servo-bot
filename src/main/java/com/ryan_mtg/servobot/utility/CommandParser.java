package com.ryan_mtg.servobot.utility;

import lombok.Getter;

import java.util.Scanner;
import java.util.regex.Pattern;

public class CommandParser {
    @Getter
    public static class ParseResult {
        private ParseStatus status;
        private String command;
        private String input;

        public ParseResult(final ParseStatus status) {
            this(status, null);
        }

        public ParseResult(final ParseStatus status, final String command) {
            this(status, command, null);
        }

        public ParseResult(final ParseStatus status, final String command, final String input) {
            this.status = status;
            this.command = command;
            this.input = input;
        }
    }

    public enum ParseStatus {
        SUCCESS,
        NO_COMMAND,
        COMMAND_MISMATCH
    }

    private Pattern commandPattern;

    public CommandParser(final Pattern commandPattern) {
        this.commandPattern = commandPattern;
    }

    public ParseResult parse(final String input) {
        Scanner scanner = new Scanner(input);

        if (!scanner.hasNext()) {
            return new ParseResult(ParseStatus.NO_COMMAND);
        }

        String command = scanner.next();
        if (command.length() <= 1) {
            return new ParseResult(ParseStatus.NO_COMMAND);
        }

        if (!commandPattern.matcher(command).matches()) {
            return new ParseResult(ParseStatus.COMMAND_MISMATCH, command);
        }

        scanner.useDelimiter("\\z");
        if (!scanner.hasNext()) {
            return new ParseResult(ParseStatus.SUCCESS, command);
        }

        String commandInput = scanner.next().trim();
        return new ParseResult(ParseStatus.SUCCESS, command, commandInput);
    }
}
