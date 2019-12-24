package com.ryan_mtg.servobot.commands;

public class Trigger {
    private int id;
    private String alias;

    public Trigger(final int id, final String alias) {
        this.id = id;
        this.alias = alias;
    }

    public int getId() {
        return id;
    }

    public String getAlias() {
        return alias;
    }
}
