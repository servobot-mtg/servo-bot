package com.ryan_mtg.servobot.model.books;

import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.utility.Validation;
import lombok.Getter;
import lombok.Setter;

public class Statement {
    public static final int UNREGISTERED_ID = 0;

    @Getter @Setter
    private int id;

    @Getter @Setter
    private String text;

    public Statement(final int id, final String text) throws UserError {
        this.id = id;
        this.text = text;

        Validation.validateStringLength(text, Validation.MAX_STATEMENT_LENGTH, "Statement text");
    }
}
