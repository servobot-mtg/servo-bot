package com.ryan_mtg.servobot.discord.commands;

public interface CommandVisitor {
    void visitFactsCommand(FactsCommand factsCommand);
    void visitMessageChannelCommmand(MessageChannelCommand messageChannelCommand);
    void visitTextCommand(TextCommand textCommand);
    void visitTierCommand(TierCommand tierCommand);
}
