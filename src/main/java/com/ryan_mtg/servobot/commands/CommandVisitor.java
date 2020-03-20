package com.ryan_mtg.servobot.commands;

public interface CommandVisitor {
    void visitAddCommand(AddCommand addCommand);
    void visitAddReactionCommand(AddReactionCommand addReactionCommand);
    void visitAddStatementCommand(AddStatementCommand addStatementCommand);
    void visitDelayedAlertCommand(DelayedAlertCommand delayedAlertCommand);
    void visitDeleteCommand(DeleteCommand deleteCommand);
    void visitEnterGiveawayCommand(EnterRaffleCommand enterRaffleCommand);
    void visitEvaluateExpressionCommand(EvaluateExpressionCommand evaluateExpressionCommand);
    void visitFactsCommand(FactsCommand factsCommand);
    void visitGameQueueCommand(GameQueueCommand gameQueueCommand);
    void visitGiveawayStatusCommand(RaffleStatusCommand raffleStatusCommand);
    void visitJailCommand(JailCommand jailCommand);
    void visitJailBreakCommand(JailBreakCommand jailBreakCommand);
    void visitJailReleaseCommand(JailReleaseCommand jailReleaseCommand);
    void visitJoinGameQueueCommand(JoinGameQueueCommand joinGameQueueCommand);
    void visitMessageChannelCommand(MessageChannelCommand messageChannelCommand);
    void visitRemoveFromGameQueueCommand(RemoveFromGameQueueCommand removeFromGameQueueCommand);
    void visitRequestPrizeCommand(RequestPrizeCommand requestPrizeCommand);
    void visitSelectWinnerCommand(SelectWinnerCommand selectWinnerCommand);
    void visitSetArenaUsernameCommand(SetArenaUsernameCommand setArenaUsernameCommand);
    void visitSetRoleCommand(SetRoleCommand setRoleCommand);
    void visitSetStatusCommand(SetStatusCommand setStatusCommand);
    void visitSetUsersRoleCommand(SetUsersRoleCommand setUsersRoleCommand);
    void visitSetValueCommand(SetValueCommand setValueCommand);
    void visitShowArenaUsernamesCommand(ShowArenaUsernamesCommand showArenaUsernamesCommand);
    void visitShowGameQueueCommand(ShowGameQueueCommand showGameQueueCommand);
    void visitShowValueCommand(ShowValueCommand showValueCommand);
    void visitStartGiveawayCommand(StartRaffleCommand startRaffleCommand);
    void visitTextCommand(TextCommand textCommand);
    void visitTierCommand(TierCommand tierCommand);
}
