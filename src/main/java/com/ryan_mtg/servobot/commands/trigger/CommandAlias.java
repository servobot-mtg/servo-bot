package com.ryan_mtg.servobot.commands.trigger;

import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.utility.Validation;
import lombok.Getter;

import java.util.Objects;
import java.util.regex.Pattern;

public class CommandAlias extends Trigger {
    public static final int TYPE = 1;
    private static final Pattern ALIAS_PATTERN = Pattern.compile("[a-zA-Z_][a-zA-Z0-9_]*");

    @Getter
    private String alias;

    public CommandAlias(final int id, final String alias) throws UserError {
        super(id);
        this.alias = alias;

        validateAlias(alias);
    }

    public static void validateAlias(final String alias) throws UserError {
        Validation.validateStringValue(alias, Validation.MAX_TRIGGER_LENGTH, "Command alias", ALIAS_PATTERN);
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
