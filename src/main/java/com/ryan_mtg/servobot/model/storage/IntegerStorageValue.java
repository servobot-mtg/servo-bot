package com.ryan_mtg.servobot.model.storage;

import com.ryan_mtg.servobot.events.BotErrorException;

public class IntegerStorageValue extends StorageValue {
    public static final int TYPE = 1;
    private int value;

    public IntegerStorageValue(final int id, final int userId, final String name, final int value)
            throws BotErrorException {
        super(id, userId, name);
        this.value = value;
    }

    @Override
    public int getType() {
        return TYPE;
    }

    @Override
    public String getTypeName() {
        return "Integer";
    }

    @Override
    public Integer getValue() {
        return value;
    }

    public void setValue(final int value) {
        this.value = value;
    }

    @Override
    public String evaluate() {
        return Integer.toString(value);
    }
}
