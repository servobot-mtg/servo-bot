package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.model.Home;

import java.time.Duration;

public class DelayedAlertCommand extends HomeCommand {
    public static final int TYPE = 16;
    private Duration delay;
    private String alertToken;

    public DelayedAlertCommand(final int id, final boolean secure, final Permission permission,
                               final Duration delay, final String alertToken) {
        super(id, secure, permission);
        this.delay = delay;
        this.alertToken = alertToken;
    }

    public Duration getDelay() {
        return delay;
    }

    public String getAlertToken() {
        return alertToken;
    }

    @Override
    public void perform(final Home home) {
        home.getHomeEditor().scheduleAlert(delay, alertToken);
    }

    @Override
    public int getType() {
        return TYPE;
    }

    @Override
    public void acceptVisitor(final CommandVisitor commandVisitor) {
        commandVisitor.visitDelayedAlertCommand(this);
    }
}
