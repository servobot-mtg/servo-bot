package com.ryan_mtg.servobot.commands;

public abstract class Command {
    public static final int UNREGISTERED_ID = 0;
    private int id;
    private boolean secure;

    public Command(final int id, final boolean secure) {
        this.id = id;
        this.secure = secure;
    }

    public final int getId() {
        return id;
    }

    public boolean isSecure() {
        return secure;
    }

    public void setSecure(final boolean secure) {
        this.secure = secure;
    }

    public abstract int getType();
    public abstract String getName();
    public abstract void acceptVisitor(CommandVisitor commandVisitor);
}
