package com.ryan_mtg.servobot.model;

import com.ryan_mtg.servobot.data.models.StatementRow;
import com.ryan_mtg.servobot.events.BotErrorException;

public class Statement {
    public static final int UNREGISTERED_ID = 0;

    private static final int MAX_TEXT_SIZE = StatementRow.MAX_TEXT_SIZE;

    private int id;
    private String text;

    public Statement(final int id, final String text) throws BotErrorException {
        this.id = id;
        this.text = text;

        if (text.length() > MAX_TEXT_SIZE) {
            throw new BotErrorException(String.format("Text too long (max %d): %s", MAX_TEXT_SIZE, text));
        }
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
