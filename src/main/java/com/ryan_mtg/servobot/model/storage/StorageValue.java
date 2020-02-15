package com.ryan_mtg.servobot.model.storage;

import com.ryan_mtg.servobot.data.models.StorageValueRow;
import com.ryan_mtg.servobot.events.BotErrorException;

import java.util.regex.Pattern;

public abstract class StorageValue {
    public static final int UNREGISTERED_ID = 0;
    private static final Pattern STORAGE_VALUE_NAME_PATTERN = Pattern.compile("[a-zA-Z0-9]+");
    private static final int MAX_NAME_SIZE = StorageValueRow.MAX_NAME_SIZE;

    private int id;
    private String name;

    public StorageValue(final int id, final String name) throws BotErrorException {
        this.id = id;
        this.name = name;

        validateName(name);
    }

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public abstract int getType();
    public abstract String getTypeName();
    public abstract Object getValue();

    public static void validateName(final String name) throws BotErrorException {
        if (!StorageValue.STORAGE_VALUE_NAME_PATTERN.matcher(name).matches()) {
            throw new BotErrorException(String.format("%s doesn't look like a value name.", name));
        }

        if (name.length() > MAX_NAME_SIZE) {
            throw new BotErrorException(String.format("Name too long (max %d): %s", MAX_NAME_SIZE, name));
        }
    }
}
