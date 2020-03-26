package com.ryan_mtg.servobot.utility;

import com.ryan_mtg.servobot.commands.CommandTable;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.model.giveaway.GiveawayCommandSettings;

import java.util.regex.Pattern;

public class Validation {
    public static final int MAX_TRIGGER_LENGTH = 30;
    public static final int MAX_EMOTE_LENGTH = 30;
    public static final int MAX_ROLE_LENGTH = 50;
    public static final int MAX_STORAGE_NAME_LENGTH = 30;
    public static final int MAX_CHANNEL_NAME_LENGTH = 50;
    public static final int MAX_TEXT_LENGTH = 200;
    public static final int MAX_NAME_LENGTH = 30;
    public static final int MAX_TIME_ZONE_LENGTH = 60;
    public static final int MAX_PATTERN_LENGTH = 30;
    public static final int MAX_AUTHENTICATION_TOKEN_LENGTH = 60;
    public static final int MAX_CLIENT_ID_LENGTH = 30;
    public static final int MAX_CLIENT_SECRET_LENGTH = 30;
    public static final int MAX_STATEMENT_LENGTH = 256;
    public static final int MAX_USERNAME_LENGTH = 50;

    public static final Pattern NAME_PATTERN = Pattern.compile("[a-zA-Z_][a-zA-Z0-9_]*");

    private Validation(){}

    public static void validateStringLength(final String string, final int maxLength, final String name)
            throws BotErrorException {
        if (string != null && string.length() > maxLength) {
            throw new BotErrorException(String.format("%s too long (max %d): %s", name, maxLength, string));
        }
    }

    public static void validateStringValue(final String value, final int maxLength, final String name,
            final Pattern valuePattern) throws BotErrorException {
        if (value != null && !valuePattern.matcher(value).matches()) {
            throw new BotErrorException(String.format("Invalid %s pattern: %s", name, value));
        }

        Validation.validateStringLength(value, maxLength, name);
    }

    public static void validateSetTemporaryCommandName(final String newCommandName, final String savedCommandName,
            final CommandTable commandTable, final boolean required, final String commandDescription)
            throws BotErrorException {
        if (required) {
            if (newCommandName == null || newCommandName.isEmpty()) {
                throw new BotErrorException(String.format("%s must not be empty", commandDescription));
            }
        }

        if (newCommandName != null && newCommandName.equals(savedCommandName)) {
            return;
        }

        if (newCommandName == null || newCommandName.isEmpty()) {
            return;
        }

        validateStringValue(newCommandName, MAX_NAME_LENGTH, commandDescription, NAME_PATTERN);
        if (commandTable.getCommand(newCommandName) != null) {
            throw new BotErrorException(String.format("There is already a '%s' command.", newCommandName));
        }
    }

    public static void validateNotSame(final String string, final String otherString, final String description,
                                       final String otherDescription) throws BotErrorException {
        if(string.equals(otherString)) {
            throw new BotErrorException(String.format("%s cannot be the same as %s", description, otherDescription));
        }
    }

    public static void validateRange(final int value, final String description, final int lowerBound,
                                     final int upperBound) throws BotErrorException {
        if (value < lowerBound) {
            throw new BotErrorException(String.format("%s (%d) is less than the lower bound (%d) ",
                    description, value, lowerBound));
        } else if (upperBound < value) {
            throw new BotErrorException(String.format("%s (%d) is greater than the upper bound (%d) ",
                    description, value, upperBound));
        }
    }

    public static void validateCommandSettings(final GiveawayCommandSettings settings, final GiveawayCommandSettings previousSettings,
                                               final CommandTable commandTable, final boolean required, final String description) throws BotErrorException {
        Validation.validateSetTemporaryCommandName(settings.getCommandName(), previousSettings.getCommandName(),
                commandTable, required, description);
    }
}
