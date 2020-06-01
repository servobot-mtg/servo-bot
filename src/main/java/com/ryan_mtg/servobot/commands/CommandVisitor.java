package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.commands.chat.AddCommand;
import com.ryan_mtg.servobot.commands.chat.AddReactionCommand;
import com.ryan_mtg.servobot.commands.chat.DeleteCommand;
import com.ryan_mtg.servobot.commands.chat.FactsCommand;
import com.ryan_mtg.servobot.commands.chat.MessageChannelCommand;
import com.ryan_mtg.servobot.commands.chat.TextCommand;
import com.ryan_mtg.servobot.commands.game_queue.GameQueueCommand;
import com.ryan_mtg.servobot.commands.game_queue.JoinGameQueueCommand;
import com.ryan_mtg.servobot.commands.game_queue.RemoveFromGameQueueCommand;
import com.ryan_mtg.servobot.commands.game_queue.ShowGameQueueCommand;
import com.ryan_mtg.servobot.commands.giveaway.EnterRaffleCommand;
import com.ryan_mtg.servobot.commands.giveaway.RaffleStatusCommand;
import com.ryan_mtg.servobot.commands.giveaway.RequestPrizeCommand;
import com.ryan_mtg.servobot.commands.giveaway.SelectWinnerCommand;
import com.ryan_mtg.servobot.commands.giveaway.StartRaffleCommand;
import com.ryan_mtg.servobot.commands.jail.ArrestCommand;
import com.ryan_mtg.servobot.commands.jail.JailBreakCommand;
import com.ryan_mtg.servobot.commands.jail.JailCommand;
import com.ryan_mtg.servobot.commands.jail.JailReleaseCommand;
import com.ryan_mtg.servobot.commands.magic.CardSearchCommand;
import com.ryan_mtg.servobot.commands.magic.ScryfallSearchCommand;

public interface CommandVisitor {
    void visitAddCommand(AddCommand addCommand);
    void visitAddReactionCommand(AddReactionCommand addReactionCommand);
    void visitAddStatementCommand(AddStatementCommand addStatementCommand);
    void visitArrestCommand(ArrestCommand arrestCommand);
    void visitCardSearchCommand(CardSearchCommand cardSearchCommand);
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
    void visitScryfallSearchCommand(ScryfallSearchCommand scryfallSearchCommand);
    void visitScoreCommand(ScoreCommand scoreCommand);
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
