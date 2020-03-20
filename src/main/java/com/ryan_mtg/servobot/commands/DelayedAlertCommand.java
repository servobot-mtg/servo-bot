package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.commands.hierarchy.HomeCommand;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.HomeEvent;
import com.ryan_mtg.servobot.model.alerts.Alert;
import com.ryan_mtg.servobot.utility.Validation;

import java.time.Duration;

public class DelayedAlertCommand extends HomeCommand {
    public static final int TYPE = 16;
    private Alert alert;

    public DelayedAlertCommand(final int id, final int flags, final Permission permission,
                               final Duration delay, final String alertToken) throws BotErrorException {
        super(id, flags, permission);
        alert = new Alert(delay, alertToken);

        Validation.validateStringLength(alertToken, Validation.MAX_TRIGGER_LENGTH, "Alert token");
    }

    public Duration getDelay() {
        return alert.getDelay();
    }

    public String getAlertToken() {
        return alert.getToken();
    }

    @Override
    public void perform(final HomeEvent homeEvent) {
        homeEvent.getHomeEditor().scheduleAlert(alert);
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
