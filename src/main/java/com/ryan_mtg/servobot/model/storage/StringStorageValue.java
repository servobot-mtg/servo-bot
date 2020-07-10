package com.ryan_mtg.servobot.model.storage;

import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.utility.Validation;
import lombok.Setter;

public class StringStorageValue extends StorageValue {
    public static final int TYPE = 2;

    @Setter
    private String value;

    public StringStorageValue(final int id, final int userId, final String name, final String value) throws UserError {
        super(id, userId, name);
        this.value = value;

        Validation.validateStringLength(value, Validation.MAX_TEXT_LENGTH, "Value");
    }

    @Override
    public int getType() {
        return TYPE;
    }

    @Override
    public String getTypeName() {
        return "String";
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String evaluate() {
        return value;
    }
}
