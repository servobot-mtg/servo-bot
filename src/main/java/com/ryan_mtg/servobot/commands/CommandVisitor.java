package com.ryan_mtg.servobot.commands;

public interface CommandVisitor {
    void visitFactsCommand(FactsCommand factsCommand);
    void visitMessageChannelCommand(MessageChannelCommand messageChannelCommand);
    void visitTextCommand(TextCommand textCommand);
    void visitTierCommand(TierCommand tierCommand);
}
