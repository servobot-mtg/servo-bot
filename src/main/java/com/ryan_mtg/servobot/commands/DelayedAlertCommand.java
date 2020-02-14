package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.model.Home;
import com.ryan_mtg.servobot.model.alerts.Alert;

import java.time.Duration;

public class DelayedAlertCommand extends HomeCommand {
    public static final int TYPE = 16;
    private Alert alert;

    public DelayedAlertCommand(final int id, final int flags, final Permission permission,
                               final Duration delay, final String alertToken) {
        super(id, flags, permission);
        alert = new Alert(delay, alertToken);
    }

    public Duration getDelay() {
        return alert.getDelay();
    }

    public String getAlertToken() {
        return alert.getToken();
    }

    @Override
    public void perform(final Home home) {
        home.getHomeEditor().scheduleAlert(alert);
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
