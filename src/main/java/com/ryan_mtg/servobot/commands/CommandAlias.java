package com.ryan_mtg.servobot.commands;

import java.util.Objects;

public class CommandAlias extends Trigger {
    public static final int TYPE = 1;

    private String alias;

    public CommandAlias(final int id, final String alias) {
        super(id);
        this.alias = alias;
    }

    public String getAlias() {
        return alias;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommandAlias that = (CommandAlias) o;
        return getId() == that.getId() &&
                alias.equals(that.alias);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), alias);
    }

    @Override
    public int getType() {
        return 1;
    }

    @Override
    public void acceptVisitor(final TriggerVisitor triggerVisitor) {
        triggerVisitor.visitCommandAlias(this);
    }
}
