package com.ryan_mtg.servobot.data.factories;

import com.ryan_mtg.servobot.commands.AddCommand;
import com.ryan_mtg.servobot.commands.AddReactionCommand;
import com.ryan_mtg.servobot.commands.AddStatementCommand;
import com.ryan_mtg.servobot.commands.CommandAlert;
import com.ryan_mtg.servobot.commands.CommandAlias;
import com.ryan_mtg.servobot.commands.CommandEvent;
import com.ryan_mtg.servobot.commands.DelayedAlertCommand;
import com.ryan_mtg.servobot.commands.DeleteCommand;
import com.ryan_mtg.servobot.commands.EnterRaffleCommand;
import com.ryan_mtg.servobot.commands.EvaluateExpressionCommand;
import com.ryan_mtg.servobot.commands.GameQueueCommand;
import com.ryan_mtg.servobot.commands.RaffleStatusCommand;
import com.ryan_mtg.servobot.commands.JailBreakCommand;
import com.ryan_mtg.servobot.commands.JailCommand;
import com.ryan_mtg.servobot.commands.JoinGameQueueCommand;
import com.ryan_mtg.servobot.commands.Permission;
import com.ryan_mtg.servobot.commands.RemoveFromGameQueueCommand;
import com.ryan_mtg.servobot.commands.RequestPrizeCommand;
import com.ryan_mtg.servobot.commands.SelectWinnerCommand;
import com.ryan_mtg.servobot.commands.SetArenaUsernameCommand;
import com.ryan_mtg.servobot.commands.SetRoleCommand;
import com.ryan_mtg.servobot.commands.SetStatusCommand;
import com.ryan_mtg.servobot.commands.SetUsersRoleCommand;
import com.ryan_mtg.servobot.commands.SetValueCommand;
import com.ryan_mtg.servobot.commands.ShowArenaUsernamesCommand;
import com.ryan_mtg.servobot.commands.ShowGameQueueCommand;
import com.ryan_mtg.servobot.commands.ShowValueCommand;
import com.ryan_mtg.servobot.commands.StartRaffleCommand;
import com.ryan_mtg.servobot.commands.Trigger;
import com.ryan_mtg.servobot.commands.TriggerVisitor;
import com.ryan_mtg.servobot.data.models.CommandRow;
import com.ryan_mtg.servobot.data.models.ReactionCommandRow;
import com.ryan_mtg.servobot.data.models.TriggerRow;
import com.ryan_mtg.servobot.data.repositories.CommandRepository;
import com.ryan_mtg.servobot.commands.Command;
import com.ryan_mtg.servobot.commands.CommandVisitor;
import com.ryan_mtg.servobot.commands.FactsCommand;
import com.ryan_mtg.servobot.commands.MessageChannelCommand;
import com.ryan_mtg.servobot.commands.TextCommand;
import com.ryan_mtg.servobot.commands.TierCommand;
import com.ryan_mtg.servobot.data.repositories.TriggerRepository;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.model.Book;
import com.ryan_mtg.servobot.model.reaction.ReactionCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.function.Consumer;

@Component
public class CommandSerializer {
    private static Logger LOGGER = LoggerFactory.getLogger(CommandSerializer.class);

    @Autowired
    private CommandRepository commandRepository;

    @Autowired
    private TriggerRepository triggerRepository;

    public Command createCommand(final CommandRow commandRow, final Map<Integer, Book> bookMap)
            throws BotErrorException {
        int id = commandRow.getId();
        int flags = commandRow.getFlags();
        Permission permission = commandRow.getPermission();
        switch (commandRow.getType()) {
            case AddCommand.TYPE:
                return new AddCommand(id, flags, permission);
            case AddReactionCommand.TYPE:
                return new AddReactionCommand(id, flags, permission, commandRow.getStringParameter().trim());
            case AddStatementCommand.TYPE:
                return new AddStatementCommand(id, flags, permission);
            case DelayedAlertCommand.TYPE:
                return new DelayedAlertCommand(id, flags, permission,
                        Duration.ofSeconds(commandRow.getLongParameter()), commandRow.getStringParameter().trim());
            case DeleteCommand.TYPE:
                return new DeleteCommand(id, flags, permission);
            case EnterRaffleCommand.TYPE:
                int giveawayId = (int) (long) commandRow.getLongParameter();
                return new EnterRaffleCommand(id, flags, permission, giveawayId,
                        commandRow.getStringParameter().trim());
            case EvaluateExpressionCommand.TYPE:
                boolean gabyEasterEgg = commandRow.getLongParameter() != null && commandRow.getLongParameter() != 0;
                return new EvaluateExpressionCommand(id, flags, permission, gabyEasterEgg);
            case FactsCommand.TYPE:
                int bookId = (int) (long) commandRow.getLongParameter();
                return new FactsCommand(id, flags, permission, bookMap.get(bookId));
            case RaffleStatusCommand.TYPE:
                giveawayId = (int) (long) commandRow.getLongParameter();
                return new RaffleStatusCommand(id, flags, permission, giveawayId);
            case GameQueueCommand.TYPE:
                int gameQueueId = (int) (long) commandRow.getLongParameter();
                return new GameQueueCommand(id, flags, permission, gameQueueId);
            case JailCommand.TYPE:
                int jailThreshold = (int) (long) commandRow.getLongParameter();
                return new JailCommand(id, flags, permission, commandRow.getStringParameter().trim(), jailThreshold,
                        commandRow.getStringParameter2().trim());
            case JailBreakCommand.TYPE:
                return new JailBreakCommand(id, flags, permission, commandRow.getStringParameter().trim(),
                        commandRow.getStringParameter2().trim());
            case JoinGameQueueCommand.TYPE:
                gameQueueId = (int) (long) commandRow.getLongParameter();
                return new JoinGameQueueCommand(id, flags, permission, gameQueueId);
            case MessageChannelCommand.TYPE:
                return new MessageChannelCommand(id, flags, permission, commandRow.getLongParameter().intValue(),
                        commandRow.getStringParameter().trim(), commandRow.getStringParameter2().trim());
            case RemoveFromGameQueueCommand.TYPE:
                gameQueueId = (int) (long) commandRow.getLongParameter();
                return new RemoveFromGameQueueCommand(id, flags, permission, gameQueueId);
            case RequestPrizeCommand.TYPE:
                giveawayId = (int) (long) commandRow.getLongParameter();
                return new RequestPrizeCommand(id, flags, permission, giveawayId);
            case SelectWinnerCommand.TYPE:
                giveawayId = (int) (long) commandRow.getLongParameter();
                return new SelectWinnerCommand(id, flags, permission, giveawayId);
            case SetArenaUsernameCommand.TYPE:
                return new SetArenaUsernameCommand(id, flags, permission);
            case SetRoleCommand.TYPE:
                return new SetRoleCommand(id, flags, permission, commandRow.getStringParameter().trim());
            case SetStatusCommand.TYPE:
                bookId = (int) (long) commandRow.getLongParameter();
                return new SetStatusCommand(id, flags, permission, bookMap.get(bookId));
            case SetUsersRoleCommand.TYPE:
                return new SetUsersRoleCommand(id, flags, permission, commandRow.getStringParameter().trim(),
                        commandRow.getStringParameter2().trim());
            case SetValueCommand.TYPE:
                return new SetValueCommand(id, flags, permission);
            case ShowArenaUsernamesCommand.TYPE:
                return new ShowArenaUsernamesCommand(id, flags, permission);
            case ShowGameQueueCommand.TYPE:
                gameQueueId = (int) (long) commandRow.getLongParameter();
                return new ShowGameQueueCommand(id, flags, permission, gameQueueId);
            case ShowValueCommand.TYPE:
                return new ShowValueCommand(id, flags, permission);
            case StartRaffleCommand.TYPE:
                giveawayId = (int) (long) commandRow.getLongParameter();
                return new StartRaffleCommand(id, flags, permission, giveawayId,
                        commandRow.getStringParameter().trim());
            case TextCommand.TYPE:
                return new TextCommand(id, flags, permission, commandRow.getStringParameter().trim());
            case TierCommand.TYPE:
                return new TierCommand(id, flags, permission);
        }
        throw new IllegalArgumentException("Unsupported command type: " + commandRow.getType());
    }

    public Trigger createTrigger(TriggerRow triggerRow) throws BotErrorException {
        int id = triggerRow.getId();
        switch (triggerRow.getType()) {
            case CommandAlias.TYPE:
                return new CommandAlias(id, triggerRow.getText());
            case CommandEvent.TYPE:
                return new CommandEvent(id, CommandEvent.Type.valueOf(triggerRow.getText()));
            case CommandAlert.TYPE:
                return new CommandAlert(id, triggerRow.getText());
        }
        throw new IllegalArgumentException("Unsupported trigger type: " + triggerRow.getType());
    }

    public ReactionCommand createReactionCommand(final ReactionCommandRow reactionCommandRow, final Command command)
            throws BotErrorException {
        return new ReactionCommand(reactionCommandRow.getId(), command);
    }

    public void saveCommand(final int botHomeId, final Command command) {
        CommandSerializationVisitor serializer = new CommandSerializationVisitor(botHomeId);
        command.acceptVisitor(serializer);
        CommandRow commandRow = serializer.getCommandRow();
        commandRepository.save(commandRow);
        command.setId(commandRow.getId());
    }

    public void saveTrigger(final int commandId, final Trigger trigger) {
        TriggerSerializationVisitor serializer = new TriggerSerializationVisitor(commandId);
        trigger.acceptVisitor(serializer);
    }

    private class TriggerSerializationVisitor implements TriggerVisitor {
        private int commandId;

        public TriggerSerializationVisitor(final int commandId) {
            this.commandId = commandId;
        }

        @Override
        public void visitCommandAlias(final CommandAlias commandAlias) {
            TriggerRow aliasRow =
                    new TriggerRow(commandAlias.getId(), CommandAlias.TYPE, commandId, commandAlias.getAlias());
            triggerRepository.save(aliasRow);
            commandAlias.setId(aliasRow.getId());
        }

        @Override
        public void visitCommandEvent(final CommandEvent commandEvent) {
            TriggerRow eventRow = new TriggerRow(commandEvent.getId(), CommandEvent.TYPE, commandId,
                    commandEvent.getEventType().toString());
            triggerRepository.save(eventRow);
            commandEvent.setId(eventRow.getId());
        }

        @Override
        public void visitCommandAlert(CommandAlert commandAlert) {
            TriggerRow alertRow =
                    new TriggerRow(commandAlert.getId(), CommandAlert.TYPE, commandId, commandAlert.getAlertToken());
            triggerRepository.save(alertRow);
            commandAlert.setId(alertRow.getId());
        }
    }

    public Trigger getTrigger(int triggerId) throws BotErrorException {
        TriggerRow triggerRow = triggerRepository.findById(triggerId).get();
        return createTrigger(triggerRow);
    }

    private class CommandSerializationVisitor implements CommandVisitor {
        private final int botHomeId;
        private final CommandRow commandRow = new CommandRow();

        public CommandSerializationVisitor(final  int botHomeId) {
            this.botHomeId = botHomeId;
        }

        public CommandRow getCommandRow() {
            return commandRow;
        }

        @Override
        public void visitAddCommand(final AddCommand addCommand) {
            saveCommand(addCommand, commandRow -> {});
        }

        @Override
        public void visitAddReactionCommand(final AddReactionCommand addReactionCommand) {
            saveCommand(addReactionCommand, commandRow -> {
                commandRow.setStringParameter(addReactionCommand.getEmoteName());
            });
        }

        @Override
        public void visitAddStatementCommand(final AddStatementCommand addStatementCommand) {
            saveCommand(addStatementCommand, commandRow -> {});
        }

        @Override
        public void visitDelayedAlertCommand(final DelayedAlertCommand delayedAlertCommand) {
            saveCommand(delayedAlertCommand, commandRow -> {
                commandRow.setLongParameter(delayedAlertCommand.getDelay().getSeconds());
                commandRow.setStringParameter(delayedAlertCommand.getAlertToken());
            });
        }

        @Override
        public void visitDeleteCommand(final DeleteCommand deleteCommand) {
            saveCommand(deleteCommand, commandRow -> {});
        }

        @Override
        public void visitEnterGiveawayCommand(final EnterRaffleCommand enterRaffleCommand) {
            saveCommand(enterRaffleCommand, commandRow -> {
                commandRow.setLongParameter(enterRaffleCommand.getGiveawayId());
                commandRow.setStringParameter(enterRaffleCommand.getResponse());
            });
        }

        @Override
        public void visitEvaluateExpressionCommand(final EvaluateExpressionCommand evaluateExpressionCommand) {
            saveCommand(evaluateExpressionCommand, commandRow -> {});
        }

        @Override
        public void visitFactsCommand(final FactsCommand factsCommand) {
            saveCommand(factsCommand, commandRow -> {
                commandRow.setLongParameter(factsCommand.getBook().getId());
            });
        }

        @Override
        public void visitGameQueueCommand(final GameQueueCommand gameQueueCommand) {
            saveCommand(gameQueueCommand, commandRow -> {
                commandRow.setLongParameter(gameQueueCommand.getGameQueueId());
            });
        }

        @Override
        public void visitGiveawayStatusCommand(final RaffleStatusCommand raffleStatusCommand) {
            saveCommand(raffleStatusCommand, commandRow -> {
                commandRow.setLongParameter(raffleStatusCommand.getGiveawayId());
            });
        }

        @Override
        public void visitJailCommand(final JailCommand jailCommand) {
            saveCommand(jailCommand, commandRow -> {
                commandRow.setStringParameter(jailCommand.getPrisonRole());
                commandRow.setLongParameter(jailCommand.getThreshold());
                commandRow.setStringParameter2(jailCommand.getVariableName());
            });
        }

        @Override
        public void visitJailBreakCommand(final JailBreakCommand jailBreakCommand) {
            saveCommand(jailBreakCommand, commandRow -> {
                commandRow.setStringParameter(jailBreakCommand.getPrisonRole());
                commandRow.setStringParameter2(jailBreakCommand.getVariableName());
            });
        }

        @Override
        public void visitJoinGameQueueCommand(final JoinGameQueueCommand joinGameQueueCommand) {
            saveCommand(joinGameQueueCommand, commandRow -> {
                commandRow.setLongParameter(joinGameQueueCommand.getGameQueueId());
            });
        }

        @Override
        public void visitMessageChannelCommand(final MessageChannelCommand messageChannelCommand) {
            saveCommand(messageChannelCommand, commandRow -> {
                commandRow.setLongParameter(messageChannelCommand.getServiceType());
                commandRow.setStringParameter(messageChannelCommand.getChannelName());
                commandRow.setStringParameter2(messageChannelCommand.getMessage());
            });
        }

        @Override
        public void visitRemoveFromGameQueueCommand(final RemoveFromGameQueueCommand removeFromGameQueueCommand) {
            saveCommand(removeFromGameQueueCommand, commandRow -> {
                commandRow.setLongParameter(removeFromGameQueueCommand.getGameQueueId());
            });
        }

        @Override
        public void visitRequestPrizeCommand(final RequestPrizeCommand requestPrizeCommand) {
            saveCommand(requestPrizeCommand, commandRow -> {
                commandRow.setLongParameter(requestPrizeCommand.getGiveawayId());
            });
        }

        @Override
        public void visitSelectWinnerCommand(final SelectWinnerCommand selectWinnerCommand) {
            saveCommand(selectWinnerCommand, commandRow -> {
                commandRow.setLongParameter(selectWinnerCommand.getGiveawayId());
            });
        }

        @Override
        public void visitSetArenaUsernameCommand(final SetArenaUsernameCommand setArenaUsernameCommand) {
            saveCommand(setArenaUsernameCommand, commandRow -> {});
        }

        @Override
        public void visitSetRoleCommand(final SetRoleCommand setRoleCommand) {
            saveCommand(setRoleCommand, commandRow -> {
                commandRow.setStringParameter(setRoleCommand.getRole());
            });
        }

        @Override
        public void visitSetStatusCommand(final SetStatusCommand setStatusCommand) {
            saveCommand(setStatusCommand, commandRow -> {
                commandRow.setLongParameter(setStatusCommand.getBook().getId());
            });
        }

        @Override
        public void visitSetUsersRoleCommand(final SetUsersRoleCommand setUsersRoleCommand) {
            saveCommand(setUsersRoleCommand, commandRow -> {
                commandRow.setStringParameter(setUsersRoleCommand.getRole());
                commandRow.setStringParameter2(setUsersRoleCommand.getMessage());
            });
        }

        @Override
        public void visitSetValueCommand(final SetValueCommand setValueCommand) {
            saveCommand(setValueCommand, commandRow -> {});
        }

        @Override
        public void visitShowArenaUsernamesCommand(final ShowArenaUsernamesCommand showArenaUsernamesCommand) {
            saveCommand(showArenaUsernamesCommand, commandRow -> {});
        }

        @Override
        public void visitShowGameQueueCommand(final ShowGameQueueCommand showGameQueueCommand) {
            saveCommand(showGameQueueCommand, commandRow -> {
                commandRow.setLongParameter(showGameQueueCommand.getGameQueueId());
            });
        }

        @Override
        public void visitShowValueCommand(final ShowValueCommand showValueCommand) {
            saveCommand(showValueCommand, commandRow -> {});
        }

        @Override
        public void visitStartGiveawayCommand(final StartRaffleCommand startRaffleCommand) {
            saveCommand(startRaffleCommand, commandRow -> {
                commandRow.setLongParameter(startRaffleCommand.getGiveawayId());
                commandRow.setStringParameter(startRaffleCommand.getMessage());
            });
        }

        @Override
        public void visitTextCommand(final TextCommand textCommand) {
            saveCommand(textCommand, commandRow -> {
                commandRow.setStringParameter(textCommand.getText());
            });
        }

        @Override
        public void visitTierCommand(final TierCommand tierCommand) {
            saveCommand(tierCommand, commandRow -> {});
        }

        private void saveCommand(final Command command, final Consumer<CommandRow> consumer) {
            commandRow.setId(command.getId());
            commandRow.setFlags(command.getFlags());
            commandRow.setType(command.getType());
            commandRow.setBotHomeId(botHomeId);
            commandRow.setPermission(command.getPermission());
            consumer.accept(commandRow);
        }
    }
}
