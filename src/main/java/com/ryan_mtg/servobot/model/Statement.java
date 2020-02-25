package com.ryan_mtg.servobot.model;

import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.utility.Validation;

public class Statement {
    public static final int UNREGISTERED_ID = 0;

    private int id;
    private String text;

    public Statement(final int id, final String text) throws BotErrorException {
        this.id = id;
        this.text = text;

        Validation.validateStringLength(text, Validation.MAX_STATEMENT_LENGTH, "Statement text");
    }

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(final String text) {
        this.text = text;
    }
}
