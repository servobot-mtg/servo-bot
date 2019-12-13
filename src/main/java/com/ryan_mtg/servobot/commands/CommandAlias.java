package com.ryan_mtg.servobot.commands;

import java.util.Objects;

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

    public void setId(final int id) {
        this.id = id;
    }

    public String getAlias() {
        return alias;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommandAlias that = (CommandAlias) o;
        return id == that.id &&
                alias.equals(that.alias);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, alias);
    }
}
