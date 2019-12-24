package com.ryan_mtg.servobot.commands;

import java.util.Objects;

public class CommandAlert extends Trigger {
    public static final int TYPE = 3;

    private String alertToken;

    public CommandAlert(final int id, final String alertToken) {
        super(id);
        this.alertToken = alertToken;
    }

    public String getAlertToken() {
        return alertToken;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommandAlert that = (CommandAlert) o;
        return getId() == that.getId() &&
                alertToken.equals(that.alertToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), alertToken);
    }

    @Override
    public int getType() {
        return TYPE;
    }

    @Override
    public void acceptVisitor(final TriggerVisitor triggerVisitor) {
        triggerVisitor.visitCommandAlert(this);
    }
}
