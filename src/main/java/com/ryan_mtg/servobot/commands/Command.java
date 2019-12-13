package com.ryan_mtg.servobot.commands;

public abstract class Command {
    public static final int UNREGISTERED_ID = 0;
    private int id;
    private boolean secure;
    private Permission permission;

    public Command(final int id, final boolean secure, final Permission permission) {
        this.id = id;
        this.secure = secure;
        this.permission = permission;
    }

    public final int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public boolean isSecure() {
        return secure;
    }

    public void setSecure(final boolean secure) {
        this.secure = secure;
    }

    public Permission getPermission() {
        return permission;
    }

    public void setPermission(final Permission permission) {
        this.permission = permission;
    }

    public abstract int getType();
    public abstract void acceptVisitor(CommandVisitor commandVisitor);
}
