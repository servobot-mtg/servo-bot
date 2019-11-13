package com.ryan_mtg.servobot.commands;

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
}
