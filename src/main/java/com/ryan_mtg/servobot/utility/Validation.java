package com.ryan_mtg.servobot.utility;

import com.ryan_mtg.servobot.commands.CommandTable;
import com.ryan_mtg.servobot.error.UserError;
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
    public static final int MAX_CLIENT_SECRET_LENGTH = 32;
    public static final int MAX_STATEMENT_LENGTH = 256;
    public static final int MAX_USERNAME_LENGTH = 50;

    public static final Pattern NAME_PATTERN = Pattern.compile("[a-zA-Z_][a-zA-Z0-9_]*");

    private Validation(){}

    public static void validateStringLength(final String string, final int maxLength, final String name)
            throws UserError {
        if (string != null && string.length() > maxLength) {
            throw new UserError("%s too long (max %d): %s", name, maxLength, string);
        }
    }

    public static void validateStringValue(final String value, final int maxLength, final String name,
            final Pattern valuePattern) throws UserError {
        if (value != null && !valuePattern.matcher(value).matches()) {
            throw new UserError("%s is not a valid %s", value, name);
        }

        Validation.validateStringLength(value, maxLength, name);
    }

    public static void validateSetTemporaryCommandName(final String newCommandName, final String savedCommandName,
            final CommandTable commandTable, final boolean required, final String commandDescription)
            throws UserError {
        boolean isBlank = Strings.isBlank(newCommandName);

        if (required && isBlank) {
            throw new UserError("%s must not be empty", commandDescription);
        }

        if (!isBlank && newCommandName.equals(savedCommandName)) {
            return;
        }

        if (isBlank) {
            return;
        }

        validateStringValue(newCommandName, MAX_NAME_LENGTH, commandDescription, NAME_PATTERN);
        if (commandTable.getCommand(newCommandName) != null) {
            throw new UserError("There is already a '%s' command.", newCommandName);
        }
    }

    public static void validateNotSame(final String string, final String otherString, final String description,
            final String otherDescription) throws UserError {
        if(string.equals(otherString)) {
            throw new UserError("%s cannot be the same as %s", description, otherDescription);
        }
    }

    /** Checks that the value is between the lower and upper bounds, which are inclusive.
     *
     * @param value The value being checked
     * @param description A human understandable name of the value
     * @param lowerBound The lowest acceptable value
     * @param upperBound The highest acceptable value
     * @throws UserError when the value is not in the acceptable range
     */
    public static void validateRange(final int value, final String description, final int lowerBound,
            final int upperBound) throws UserError {
        if (value < lowerBound) {
            throw new UserError("%s (%d) is less than the lower bound (%d) ", description, value, lowerBound);
        } else if (upperBound < value) {
            throw new UserError("%s (%d) is greater than the upper bound (%d) ", description, value, upperBound);
        }
    }

    public static void validateCommandSettings(final GiveawayCommandSettings settings,
            final GiveawayCommandSettings previousSettings, final CommandTable commandTable,
            final boolean required, final String description) throws UserError {
        Validation.validateSetTemporaryCommandName(settings.getCommandName(), previousSettings.getCommandName(),
                commandTable, required, description);
    }
}
