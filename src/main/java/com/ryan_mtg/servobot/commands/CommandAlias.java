package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.events.BotErrorException;

import java.util.Objects;
import java.util.regex.Pattern;

public class CommandAlias extends Trigger {
    public static final int TYPE = 1;
    private static final Pattern ALIAS_PATTERN = Pattern.compile("[a-zA-Z_][a-zA-Z0-9_]*");

    private String alias;

    public CommandAlias(final int id, final String alias) throws BotErrorException {
        super(id);
        this.alias = alias;

        validateAlias(alias);
    }

    public String getAlias() {
        return alias;
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

    public static void validateAlias(final String alias) throws BotErrorException {
        if (!ALIAS_PATTERN.matcher(alias).matches()) {
            throw new BotErrorException(String.format("Invalid alias: %s", alias));
        }

        if (alias.length() > MAX_TEXT_SIZE) {
            throw new BotErrorException(String.format("Alias too long (max %d): %s", MAX_TEXT_SIZE, alias));
        }
    }
}
