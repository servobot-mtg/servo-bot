package com.ryan_mtg.servobot.commands;

public interface CommandVisitor {
    void visitAddCommand(AddCommand addCommand);
    void visitAddReactionCommand(AddReactionCommand addReactionCommand);
    void visitAddStatementCommand(AddStatementCommand addStatementCommand);
    void visitDelayedAlertCommand(DelayedAlertCommand delayedAlertCommand);
    void visitDeleteCommand(DeleteCommand deleteCommand);
    void visitEnterGiveawayCommand(EnterGiveawayCommand enterGiveawayCommand);
    void visitEvaluateExpressionCommand(EvaluateExpressionCommand evaluateExpressionCommand);
    void visitFactsCommand(FactsCommand factsCommand);
    void visitGameQueueCommand(GameQueueCommand gameQueueCommand);
    void visitGiveawayStatusCommand(GiveawayStatusCommand giveawayStatusCommand);
    void visitJailCommand(JailCommand jailCommand);
    void visitJailBreakCommand(JailBreakCommand jailBreakCommand);
    void visitJoinGameQueueCommand(JoinGameQueueCommand joinGameQueueCommand);
    void visitMessageChannelCommand(MessageChannelCommand messageChannelCommand);
    void visitRemoveFromGameQueueCommand(RemoveFromGameQueueCommand removeFromGameQueueCommand);
    void visitSelectWinnerCommand(SelectWinnerCommand selectWinnerCommand);
    void visitSetArenaUsernameCommand(SetArenaUsernameCommand setArenaUsernameCommand);
    void visitSetRoleCommand(SetRoleCommand setRoleCommand);
    void visitSetStatusCommand(SetStatusCommand setStatusCommand);
    void visitSetValueCommand(SetValueCommand setValueCommand);
    void visitShowArenaUsernamesCommand(ShowArenaUsernamesCommand showArenaUsernamesCommand);
    void visitShowGameQueueCommand(ShowGameQueueCommand showGameQueueCommand);
    void visitShowValueCommand(ShowValueCommand showValueCommand);
    void visitStartGiveawayCommand(StartGiveawayCommand startGiveawayCommand);
    void visitTextCommand(TextCommand textCommand);
    void visitTierCommand(TierCommand tierCommand);
}
