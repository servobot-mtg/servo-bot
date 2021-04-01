package com.ryan_mtg.servobot.commands.trigger;

import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.utility.Validation;
import lombok.Getter;

import java.util.Objects;
import java.util.regex.Pattern;

public class CommandAlert extends Trigger {
    public static final int TYPE = 3;
    private static final Pattern ALERT_PATTERN = Pattern.compile("[a-zA-Z_][a-zA-Z0-9_]*");

    @Getter
    private final String alertToken;

    public CommandAlert(final int id, final String alertToken) throws UserError {
        super(id);
        this.alertToken = alertToken;

        Validation.validateStringValue(alertToken, Validation.MAX_TRIGGER_LENGTH, "Alert token", ALERT_PATTERN);
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
