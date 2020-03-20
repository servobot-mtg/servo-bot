package com.ryan_mtg.servobot.commands;

import lombok.Getter;
import lombok.Setter;

public abstract class Trigger {
    public static final int UNREGISTERED_ID = 0;

    @Getter @Setter
    private int id;

    public Trigger(final int id) {
        this.id = id;
    }

    public abstract int getType();
    public abstract void acceptVisitor(TriggerVisitor triggerVisitor);
}
