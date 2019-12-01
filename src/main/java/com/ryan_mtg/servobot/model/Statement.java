package com.ryan_mtg.servobot.model;

public class Statement {
    public static final int UNREGISTERED_ID = 0;

    private int id;
    private String text;

    public Statement(final int id, final String text) {
        this.id = id;
        this.text = text;
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
