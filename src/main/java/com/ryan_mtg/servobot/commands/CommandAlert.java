package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.events.BotErrorException;

import java.util.Objects;
import java.util.regex.Pattern;

public class CommandAlert extends Trigger {
    public static final int TYPE = 3;
    private static final Pattern ALERT_PATTERN = Pattern.compile("[a-zA-Z_][a-zA-Z0-9_]*");

    private String alertToken;

    public CommandAlert(final int id, final String alertToken) throws BotErrorException {
        super(id);
        this.alertToken = alertToken;

        validateToken(alertToken);
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

    public static void validateToken(final String alertToken) throws BotErrorException {
        if (!ALERT_PATTERN.matcher(alertToken).matches()) {
            throw new BotErrorException("Invalid alert token");
        }

        if (alertToken.length() > MAX_TEXT_SIZE) {
            throw new BotErrorException(String.format("Token too long (max %d): %s", MAX_TEXT_SIZE, alertToken));
        }
    }
}
