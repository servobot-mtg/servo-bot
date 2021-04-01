package com.ryan_mtg.servobot.model.storage;

import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.utility.Validation;
import lombok.Getter;
import lombok.Setter;

import java.util.regex.Pattern;

public abstract class StorageValue implements Evaluatable {
    public static final int UNREGISTERED_ID = 0;
    public static final int GLOBAL_USER = 0;
    private static final Pattern STORAGE_VALUE_NAME_PATTERN = Pattern.compile("#?[a-z_A-Z][a-z_A-Z0-9]+");

    @Getter @Setter
    private int id;

    @Getter @Setter
    private int userId;

    @Getter
    private final String name;

    public StorageValue(final int id, final int userId, final String name) throws UserError {
        this.id = id;
        this.userId = userId;
        this.name = name;

        validateName(name);
    }

    public static void validateName(final String name) throws UserError {
        Validation.validateStringValue(name, Validation.MAX_NAME_LENGTH, "Name", STORAGE_VALUE_NAME_PATTERN);
    }

    public abstract int getType();
    public abstract String getTypeName();
    public abstract Object getValue();
}
