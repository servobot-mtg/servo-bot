package com.ryan_mtg.servobot.model.storage;

import java.util.regex.Pattern;

public abstract class StorageValue {
    public static final int UNREGISTERED_ID = 0;
    public static Pattern STORAGE_VALUE_NAME_PATTERN = Pattern.compile("[a-zA-Z0-9]+");

    private int id;
    private String name;

    public StorageValue(final int id, final String name) {
        this.id = id;
        this.name = name;
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
}
