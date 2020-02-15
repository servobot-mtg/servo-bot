package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.data.models.TriggerRow;

public abstract class Trigger {
    public static final int UNREGISTERED_ID = 0;
    protected static final int MAX_TEXT_SIZE = TriggerRow.MAX_TEXT_SIZE;

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
