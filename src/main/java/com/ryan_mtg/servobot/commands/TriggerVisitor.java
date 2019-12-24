package com.ryan_mtg.servobot.commands;

public interface TriggerVisitor {
    void visitCommandAlias(CommandAlias commandAlias);
    void visitCommandEvent(CommandEvent commandEvent);
    void visitCommandAlert(CommandAlert commandAlert);
}
