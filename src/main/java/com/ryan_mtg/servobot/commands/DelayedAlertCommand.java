package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.commands.hierarchy.CommandSettings;
import com.ryan_mtg.servobot.commands.hierarchy.HomeCommand;
import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.events.HomeEvent;
import com.ryan_mtg.servobot.model.alerts.Alert;
import com.ryan_mtg.servobot.utility.Validation;

import java.time.Duration;

public class DelayedAlertCommand extends HomeCommand {
    public static final CommandType TYPE = CommandType.DELAYED_ALERT_COMMAND_TYPE;
    private Alert alert;
    private final Duration delay;

    public DelayedAlertCommand(final int id, final CommandSettings commandSettings, final Duration delay,
            final String alertToken) throws UserError {
        super(id, commandSettings);
        this.delay = delay;
        setAlertToken(alertToken);
    }

    public void setAlertToken(final String alertToken) throws UserError {
        Validation.validateStringLength(alertToken, Validation.MAX_TRIGGER_LENGTH, "Alert token");
        alert = new Alert(delay, alertToken);
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
    public CommandType getType() {
        return TYPE;
    }

    @Override
    public void acceptVisitor(final CommandVisitor commandVisitor) {
        commandVisitor.visitDelayedAlertCommand(this);
    }
}
