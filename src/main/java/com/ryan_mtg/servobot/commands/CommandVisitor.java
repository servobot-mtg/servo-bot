package com.ryan_mtg.servobot.commands;

public interface CommandVisitor {
    void visitAddCommand(AddCommand addCommand);
    void visitDeleteCommand(DeleteCommand deleteCommand);
    void visitFactsCommand(FactsCommand factsCommand);
    void visitGameQueueCommand(GameQueueCommand gameQueueCommand);
    void visitJoinGameQueueCommand(JoinGameQueueCommand joinGameQueueCommand);
    void visitMessageChannelCommand(MessageChannelCommand messageChannelCommand);
    void visitRemoveFromGameQueueCommand(RemoveFromGameQueueCommand removeFromGameQueueCommand);
    void visitSetArenaUsernameCommand(SetArenaUsernameCommand setArenaUsernameCommand);
    void visitSetRoleCommand(SetRoleCommand setRoleCommand);
    void visitSetStatusCommand(SetStatusCommand setStatusCommand);
    void visitShowArenaUsernamesCommand(ShowArenaUsernamesCommand showArenaUsernamesCommand);
    void visitShowGameQueueCommand(ShowGameQueueCommand showGameQueueCommand);
    void visitTextCommand(TextCommand textCommand);
    void visitTierCommand(TierCommand tierCommand);
}
