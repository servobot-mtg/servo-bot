package com.ryan_mtg.servobot.commands;

public abstract class Trigger {
    public static final int UNREGISTERED_ID = 0;

    private int id;

    public Trigger(final int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public abstract int getType();
    public abstract void acceptVisitor(TriggerVisitor triggerVisitor);
}
