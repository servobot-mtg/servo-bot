package com.ryan_mtg.servobot.commands;

public abstract class Command {
    public static final int UNREGISTERED_ID = 0;
    private int id;

    public Command(final int id) {
        this.id = id;
    }

    public final int getId() {
        return id;
    }

    public abstract int getType();
    public abstract String getName();
    public abstract void acceptVisitor(CommandVisitor commandVisitor);
}
