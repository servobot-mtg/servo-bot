package com.ryan_mtg.servobot.commands;

import java.util.Objects;

public class CommandAlert {
    public static final int UNREGISTERED_ID = 0;

    private int id;

    private String alertToken;

    public CommandAlert(final int id, final String alertToken) {
        this.id = id;
        this.alertToken = alertToken;
    }

    public int getId() {
        return id;
    }

    public String getAlertToken() {
        return alertToken;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommandAlert that = (CommandAlert) o;
        return id == that.id &&
                alertToken.equals(that.alertToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, alertToken);
    }
}
