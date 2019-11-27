package com.ryan_mtg.servobot.commands;

public interface CommandVisitor {
    void visitAddCommand(AddCommand addCommand);
    void visitDeleteCommand(DeleteCommand deleteCommand);
    void visitFactsCommand(FactsCommand factsCommand);
    void visitMessageChannelCommand(MessageChannelCommand messageChannelCommand);
    void visitTextCommand(TextCommand textCommand);
    void visitTierCommand(TierCommand tierCommand);
}
