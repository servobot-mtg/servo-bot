package com.ryan_mtg.servobot.commands;

import java.util.Objects;

public class CommandEvent {
    public static final int UNREGISTERED_ID = 0;

    public enum Type {
        STREAM_START,
        NEW_USER,
    }

    private int id;

    private Type eventType;

    public CommandEvent(final int id, final Type eventType) {
        this.id = id;
        this.eventType = eventType;
    }

    public int getId() {
        return id;
    }

    public Type getEventType() {
        return eventType;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommandEvent that = (CommandEvent) o;
        return id == that.id &&
                eventType == that.eventType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, eventType);
    }
}
