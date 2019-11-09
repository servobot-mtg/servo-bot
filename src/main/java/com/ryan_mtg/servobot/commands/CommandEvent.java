package com.ryan_mtg.servobot.commands;

public class CommandEvent {
    public static final int UNREGISTERED_ID = 0;

    public enum Type {
        STREAM_START,
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
}
