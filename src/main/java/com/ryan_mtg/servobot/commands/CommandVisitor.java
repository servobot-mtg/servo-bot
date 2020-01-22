package com.ryan_mtg.servobot.commands;

public interface CommandVisitor {
    void visitAddCommand(AddCommand addCommand);
    void visitAddStatementCommand(AddStatementCommand addStatementCommand);
    void visitDelayedAlertCommand(DelayedAlertCommand delayedAlertCommand);
    void visitDeleteCommand(DeleteCommand deleteCommand);
    void visitEvaluateExpressionCommand(EvaluateExpressionCommand evaluateExpressionCommand);
    void visitFactsCommand(FactsCommand factsCommand);
    void visitGameQueueCommand(GameQueueCommand gameQueueCommand);
    void visitJoinGameQueueCommand(JoinGameQueueCommand joinGameQueueCommand);
    void visitMessageChannelCommand(MessageChannelCommand messageChannelCommand);
    void visitRemoveFromGameQueueCommand(RemoveFromGameQueueCommand removeFromGameQueueCommand);
    void visitSetArenaUsernameCommand(SetArenaUsernameCommand setArenaUsernameCommand);
    void visitSetRoleCommand(SetRoleCommand setRoleCommand);
    void visitSetStatusCommand(SetStatusCommand setStatusCommand);
    void visitSetValueCommand(SetValueCommand setValueCommand);
    void visitShowArenaUsernamesCommand(ShowArenaUsernamesCommand showArenaUsernamesCommand);
    void visitShowGameQueueCommand(ShowGameQueueCommand showGameQueueCommand);
    void visitShowValueCommand(ShowValueCommand showValueCommand);
    void visitTextCommand(TextCommand textCommand);
    void visitTierCommand(TierCommand tierCommand);
}
