package com.ryan_mtg.servobot.commands;

public class CommandAlias {
    public static final int UNREGISTERED_ID = 0;

    private int id;
    private String alias;

    public CommandAlias(final int id, final String alias) {
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
