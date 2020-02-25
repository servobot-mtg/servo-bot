package com.ryan_mtg.servobot.model.storage;

import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.utility.Validation;

import java.util.regex.Pattern;

public abstract class StorageValue {
    public static final int UNREGISTERED_ID = 0;
    public static final int GLOBAL_USER = 0;
    private static final Pattern STORAGE_VALUE_NAME_PATTERN = Pattern.compile("[a-zA-Z0-9]+");

    private int id;
    private int userId;
    private String name;

    public StorageValue(final int id, final int userId, final String name) throws BotErrorException {
        this.id = id;
        this.userId = userId;
        this.name = name;

        validateName(name);
    }

    public static void validateName(final String name) throws BotErrorException {
        Validation.validateStringValue(name, Validation.MAX_NAME_LENGTH, "Name", STORAGE_VALUE_NAME_PATTERN);
    }

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public abstract int getType();
    public abstract String getTypeName();
    public abstract Object getValue();
}
