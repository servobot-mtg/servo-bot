package com.ryan_mtg.servobot.utility;

import com.ryan_mtg.servobot.events.BotErrorException;

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


    private Validation(){}

    public static void validateStringLength(final String string, final int maxLength, final String name)
            throws BotErrorException {
        if (string != null && string.length() > maxLength) {
            throw new BotErrorException(String.format("%s too long (max %d): %s", name, maxLength, string));
        }
    }

    public static void validateStringValue(final String value, final int maxLength, final String name,
            final Pattern valuePattern) throws BotErrorException {
        if (!valuePattern.matcher(value).matches()) {
            throw new BotErrorException(String.format("Invalid %s pattern: %s", name, value));
        }

        Validation.validateStringLength(value, maxLength, name);
    }
}
