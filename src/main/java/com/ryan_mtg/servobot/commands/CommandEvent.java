package com.ryan_mtg.servobot.commands;

import java.util.Objects;

public class CommandEvent extends Trigger{
    public static final int TYPE = 2;

    public enum Type {
        STREAM_START,
        NEW_USER,
        RAID,
        SUBSCRIBE,
    }

    private Type eventType;

    public CommandEvent(final int id, final Type eventType) {
        super(id);
        this.eventType = eventType;
    }

    @Override
    public int getType() {
        return TYPE;
    }

    @Override
    public void acceptVisitor(final TriggerVisitor triggerVisitor) {
        triggerVisitor.visitCommandEvent(this);
    }

    public Type getEventType() {
        return eventType;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommandEvent that = (CommandEvent) o;
        return getId() == that.getId() &&
                eventType == that.eventType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), eventType);
    }
}
