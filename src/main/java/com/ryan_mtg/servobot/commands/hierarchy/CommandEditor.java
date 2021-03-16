package com.ryan_mtg.servobot.commands.hierarchy;

import com.ryan_mtg.servobot.commands.AddBookedStatementCommand;
import com.ryan_mtg.servobot.commands.AddStatementCommand;
import com.ryan_mtg.servobot.commands.CommandVisitor;
import com.ryan_mtg.servobot.commands.DelayedAlertCommand;
import com.ryan_mtg.servobot.commands.EvaluateExpressionCommand;
import com.ryan_mtg.servobot.commands.ScoreCommand;
import com.ryan_mtg.servobot.commands.SetArenaUsernameCommand;
import com.ryan_mtg.servobot.commands.SetStatusCommand;
import com.ryan_mtg.servobot.commands.SetValueCommand;
import com.ryan_mtg.servobot.commands.ShowArenaUsernamesCommand;
import com.ryan_mtg.servobot.commands.ShowValueCommand;
import com.ryan_mtg.servobot.commands.TierCommand;
import com.ryan_mtg.servobot.commands.chat.AddCommand;
import com.ryan_mtg.servobot.commands.chat.AddReactionCommand;
import com.ryan_mtg.servobot.commands.chat.DeleteCommand;
import com.ryan_mtg.servobot.commands.chat.FactsCommand;
import com.ryan_mtg.servobot.commands.chat.MessageChannelCommand;
import com.ryan_mtg.servobot.commands.chat.TextCommand;
import com.ryan_mtg.servobot.commands.chat_draft.BeginChatDraftCommand;
import com.ryan_mtg.servobot.commands.chat_draft.ChatDraftStatusCommand;
import com.ryan_mtg.servobot.commands.chat_draft.CloseChatDraftCommand;
import com.ryan_mtg.servobot.commands.chat_draft.EnterChatDraftCommand;
import com.ryan_mtg.servobot.commands.chat_draft.NextPickCommand;
import com.ryan_mtg.servobot.commands.chat_draft.OpenChatDraftCommand;
import com.ryan_mtg.servobot.commands.game.GameCommand;
import com.ryan_mtg.servobot.commands.game.JoinGameCommand;
import com.ryan_mtg.servobot.commands.game_queue.GameQueueCommand;
import com.ryan_mtg.servobot.commands.game_queue.JoinGameQueueCommand;
import com.ryan_mtg.servobot.commands.game_queue.RemoveFromGameQueueCommand;
import com.ryan_mtg.servobot.commands.game_queue.ShowGameQueueCommand;
import com.ryan_mtg.servobot.commands.giveaway.EnterRaffleCommand;
import com.ryan_mtg.servobot.commands.giveaway.RaffleStatusCommand;
import com.ryan_mtg.servobot.commands.giveaway.RequestPrizeCommand;
import com.ryan_mtg.servobot.commands.giveaway.SelectWinnerCommand;
import com.ryan_mtg.servobot.commands.giveaway.StartRaffleCommand;
import com.ryan_mtg.servobot.commands.magic.CardSearchCommand;
import com.ryan_mtg.servobot.commands.magic.ScryfallSearchCommand;
import com.ryan_mtg.servobot.commands.roles.MakeRoleMessageCommand;
import com.ryan_mtg.servobot.commands.roles.SetRoleCommand;
import com.ryan_mtg.servobot.commands.roles.SetUsersRoleCommand;
import com.ryan_mtg.servobot.error.SystemError;
import com.ryan_mtg.servobot.error.ThrowingRunnable;
import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.model.books.Book;
import com.ryan_mtg.servobot.model.books.BookTable;
import lombok.Getter;

public class CommandEditor {
    private final BookTable bookTable;

    public CommandEditor(final BookTable bookTable) {
        this.bookTable = bookTable;
    }

    public Command editCommand(final Command command, final String text) throws UserError {
        CommandEditVisitor commandEditVisitor = new CommandEditVisitor(text);
        command.acceptVisitor(commandEditVisitor);
        if (commandEditVisitor.getUserError() != null) {
            throw commandEditVisitor.getUserError();
        }
        return command;
    }

    private class CommandEditVisitor implements CommandVisitor {
        private String text;

        @Getter
        private UserError userError;

        public CommandEditVisitor(final String text) {
            this.text = text;
        }

        @Override
        public void visitAddBookedStatementCommand(final AddBookedStatementCommand addBookedStatementCommand) {
            throw new SystemError("AddBookedStatementCommand does not allow edits");
        }

        @Override
        public void visitAddCommand(final AddCommand addCommand) {
            throw new SystemError("AddCommand does not allow edits");
        }

        @Override
        public void visitAddReactionCommand(final AddReactionCommand addReactionCommand) {
            handleError(() -> addReactionCommand.setEmoteName(text));
        }

        @Override
        public void visitAddStatementCommand(final AddStatementCommand addStatementCommand) {
            throw new SystemError("AddStatementCommand does not allow edits");
        }

        @Override
        public void visitBeginChatDraftCommand(final BeginChatDraftCommand beginChatDraftCommand) {
            handleError(() -> beginChatDraftCommand.setResponse(text));
        }

        @Override
        public void visitCardSearchCommand(final CardSearchCommand cardSearchCommand) {
            throw new SystemError("CardSearchCommand does not allow edits");
        }

        @Override
        public void visitChatDraftStatusCommand(final ChatDraftStatusCommand chatDraftStatusCommand) {
            handleError(() -> chatDraftStatusCommand.setResponse(text));
        }

        @Override
        public void visitCloseChatDraftCommand(final CloseChatDraftCommand closeChatDraftCommand) {
            handleError(() -> closeChatDraftCommand.setResponse(text));
        }

        @Override
        public void visitDelayedAlertCommand(final DelayedAlertCommand delayedAlertCommand) {
            handleError(() -> delayedAlertCommand.setAlertToken(text));
        }

        @Override
        public void visitDeleteCommand(final DeleteCommand deleteCommand) {
            throw new SystemError("DeleteCommand does not allow edits");
        }

        @Override
        public void visitEnterChatDraftCommand(final EnterChatDraftCommand enterChatDraftCommand) {
            throw new SystemError("EnterChatDraftCommand does not allow edits");
        }

        @Override
        public void visitEnterRaffleCommand(final EnterRaffleCommand enterRaffleCommand) {
            throw new SystemError("EnterRaffleCommand does not allow edits");
        }

        @Override
        public void visitEvaluateExpressionCommand(final EvaluateExpressionCommand evaluateExpressionCommand) {
            throw new SystemError("EvaluateExpressionCommand does not allow edits");
        }

        @Override
        public void visitFactsCommand(final FactsCommand factsCommand) {
            handleError(() -> {
                Book book = bookTable.getBook(text).orElseThrow(() -> new UserError("No book named %s.", text));
                factsCommand.setBook(book);
            });
        }

        @Override
        public void visitGameCommand(final GameCommand gameCommand) {
            throw new SystemError("GameCommand does not allow edits");
        }

        @Override
        public void visitGameQueueCommand(final GameQueueCommand gameQueueCommand) {
            throw new SystemError("GameQueueCommand does not allow edits");
        }

        @Override
        public void visitGiveawayStatusCommand(final RaffleStatusCommand raffleStatusCommand) {
            throw new SystemError("RaffleStatusCommand does not allow edits");
        }

        @Override
        public void visitJoinGameCommand(final JoinGameCommand joinGameCommand) {
            throw new SystemError("JoinGameCommand does not allow edits");
        }

        @Override
        public void visitJoinGameQueueCommand(final JoinGameQueueCommand joinGameQueueCommand) {
            throw new SystemError("JoinGameQueueCommand does not allow edits");
        }

        @Override
        public void visitMakeRoleMessageCommand(final MakeRoleMessageCommand makeRoleMessageCommand) {
            throw new SystemError("MakeRoleMessageCommand does not allow edits");
        }

        @Override
        public void visitMessageChannelCommand(final MessageChannelCommand messageChannelCommand) {
            handleError(() -> messageChannelCommand.setMessage(text));
        }

        @Override
        public void visitNextPickCommand(final NextPickCommand nextPickCommand) {
            handleError(() -> nextPickCommand.setResponse(text));
        }

        @Override
        public void visitOpenChatDraftCommand(final OpenChatDraftCommand openChatDraftCommand) {
            handleError(() -> openChatDraftCommand.setResponse(text));
        }

        @Override
        public void visitRemoveFromGameQueueCommand(final RemoveFromGameQueueCommand removeFromGameQueueCommand) {
            throw new SystemError("RemoveFromGameQueueCommand does not allow edits");
        }

        @Override
        public void visitRequestPrizeCommand(final RequestPrizeCommand requestPrizeCommand) {
            throw new SystemError("RequestPrizeCommand does not allow edits");
        }

        @Override
        public void visitScryfallSearchCommand(final ScryfallSearchCommand scryfallSearchCommand) {
            throw new SystemError("ScryfallSearchCommand does not allow edits");
        }

        @Override
        public void visitScoreCommand(final ScoreCommand scoreCommand) {
            throw new SystemError("ScoreCommand does not allow edits");
        }

        @Override
        public void visitSelectWinnerCommand(final SelectWinnerCommand selectWinnerCommand) {
            throw new SystemError("SelectWinnerCommand does not allow edits");
        }

        @Override
        public void visitSetArenaUsernameCommand(final SetArenaUsernameCommand setArenaUsernameCommand) {
            throw new SystemError("SetArenaUsernameCommand does not allow edits");
        }

        @Override
        public void visitSetRoleCommand(final SetRoleCommand setRoleCommand) {
            throw new SystemError("SetRoleCommand does not allow edits");
        }

        @Override
        public void visitSetStatusCommand(final SetStatusCommand setStatusCommand) {
            handleError(() -> {
                Book book = bookTable.getBook(text).orElseThrow(() -> new UserError("No book named %s.", text));
                setStatusCommand.setBook(book);
            });
        }

        @Override
        public void visitSetUsersRoleCommand(final SetUsersRoleCommand setUsersRoleCommand) {
            handleError(() -> setUsersRoleCommand.setRole(text));
        }

        @Override
        public void visitSetValueCommand(final SetValueCommand setValueCommand) {
            throw new SystemError("SetValueCommand does not allow edits");
        }

        @Override
        public void visitShowArenaUsernamesCommand(final ShowArenaUsernamesCommand showArenaUsernamesCommand) {
            throw new SystemError("ShowArenaUsernamesCommand does not allow edits");
        }

        @Override
        public void visitShowGameQueueCommand(final ShowGameQueueCommand showGameQueueCommand) {
            throw new SystemError("ShowGameQueueCommand does not allow edits");
        }

        @Override
        public void visitShowValueCommand(final ShowValueCommand showValueCommand) {
            throw new SystemError("ShowValueCommand does not allow edits");
        }

        @Override
        public void visitStartGiveawayCommand(final StartRaffleCommand startRaffleCommand) {
            throw new SystemError("StartRaffleCommand does not allow edits");
        }

        @Override
        public void visitTextCommand(final TextCommand textCommand) {
            handleError(() -> textCommand.setText(text));
        }

        @Override
        public void visitTierCommand(final TierCommand tierCommand) {
            throw new SystemError("TierCommand does not allow edits");
        }

        private void handleError(final ThrowingRunnable runnable) {
            try {
                runnable.run();
            } catch (UserError userError) {
                this.userError = userError;
            } catch (Exception e) {
                throw new SystemError(e, "Unhandled error: ");
            }
        }
    }
}