package com.ryan_mtg.servobot.utility;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class CommandParser {
    @Getter
    public static class ParseResult {
        private ParseStatus status;
        private String command;
        private String input;
        private List<String> flags;

        public ParseResult(final ParseStatus status) {
            this(status, null);
        }

        public ParseResult(final ParseStatus status, final String command) {
            this(status, command, null);
        }

        public ParseResult(final ParseStatus status, final String command, final String input) {
            this(status, command, input, Collections.emptyList());
        }

        public ParseResult(final ParseStatus status, final String command, final String input,
                           final List<String> flags) {
            this.status = status;
            this.command = command;
            this.input = input;
            this.flags = flags;
        }
    }

    public enum ParseStatus {
        SUCCESS,
        NO_COMMAND,
        COMMAND_MISMATCH
    }

    private Pattern commandPattern;
    private Pattern flagPattern;

    public CommandParser(final Pattern commandPattern) {
        this(commandPattern, null);
    }

    public CommandParser(final Pattern commandPattern, final Pattern flagPattern) {
        this.commandPattern = commandPattern;
        this.flagPattern = flagPattern;
    }

    public ParseResult parse(final String input) {
        if (Strings.isBlank(input)) {
            return new ParseResult(ParseStatus.NO_COMMAND);
        }

        Scanner scanner = new Scanner(input);

        if (!scanner.hasNext()) {
            return new ParseResult(ParseStatus.NO_COMMAND);
        }

        String command = scanner.next();
        if (!commandPattern.matcher(command).matches()) {
            return new ParseResult(ParseStatus.COMMAND_MISMATCH, command);
        }

        scanner.useDelimiter("\\z");
        if (!scanner.hasNext()) {
            return new ParseResult(ParseStatus.SUCCESS, command);
        }

        String commandInput = scanner.next().trim();

        if (flagPattern == null) {
            return new ParseResult(ParseStatus.SUCCESS, command, commandInput);
        }

        List<String> flags = new ArrayList<>();
        while (true) {
            Scanner flagScanner = new Scanner(commandInput);

            if (!flagScanner.hasNext()) {
                break;
            }
            String possibleFlag = flagScanner.next();

            if (!flagPattern.matcher(possibleFlag).matches()) {
                break;
            }
            flags.add(possibleFlag);

            flagScanner.useDelimiter("\\z");
            if (!flagScanner.hasNext()) {
                return new ParseResult(ParseStatus.SUCCESS, command, null, flags);
            }

            commandInput = flagScanner.next().trim();
        }

        return new ParseResult(ParseStatus.SUCCESS, command, commandInput, flags);
    }
}
