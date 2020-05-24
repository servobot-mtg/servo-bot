package com.ryan_mtg.servobot.data.factories;

import com.ryan_mtg.servobot.commands.hierarchy.CommandSettings;
import com.ryan_mtg.servobot.commands.CommandType;
import com.ryan_mtg.servobot.commands.chat.AddCommand;
import com.ryan_mtg.servobot.commands.chat.AddReactionCommand;
import com.ryan_mtg.servobot.commands.AddStatementCommand;
import com.ryan_mtg.servobot.commands.hierarchy.RateLimit;
import com.ryan_mtg.servobot.commands.jail.ArrestCommand;
import com.ryan_mtg.servobot.commands.magic.CardSearchCommand;
import com.ryan_mtg.servobot.commands.magic.ScryfallSearchCommand;
import com.ryan_mtg.servobot.commands.trigger.CommandAlert;
import com.ryan_mtg.servobot.commands.trigger.CommandAlias;
import com.ryan_mtg.servobot.commands.trigger.CommandEvent;
import com.ryan_mtg.servobot.commands.DelayedAlertCommand;
import com.ryan_mtg.servobot.commands.chat.DeleteCommand;
import com.ryan_mtg.servobot.commands.giveaway.EnterRaffleCommand;
import com.ryan_mtg.servobot.commands.EvaluateExpressionCommand;
import com.ryan_mtg.servobot.commands.game_queue.GameQueueCommand;
import com.ryan_mtg.servobot.commands.jail.JailBreakCommand;
import com.ryan_mtg.servobot.commands.jail.JailCommand;
import com.ryan_mtg.servobot.commands.jail.JailReleaseCommand;
import com.ryan_mtg.servobot.commands.game_queue.JoinGameQueueCommand;
import com.ryan_mtg.servobot.commands.giveaway.RaffleStatusCommand;
import com.ryan_mtg.servobot.commands.game_queue.RemoveFromGameQueueCommand;
import com.ryan_mtg.servobot.commands.giveaway.RequestPrizeCommand;
import com.ryan_mtg.servobot.commands.giveaway.SelectWinnerCommand;
import com.ryan_mtg.servobot.commands.SetArenaUsernameCommand;
import com.ryan_mtg.servobot.commands.SetRoleCommand;
import com.ryan_mtg.servobot.commands.SetStatusCommand;
import com.ryan_mtg.servobot.commands.SetUsersRoleCommand;
import com.ryan_mtg.servobot.commands.SetValueCommand;
import com.ryan_mtg.servobot.commands.ShowArenaUsernamesCommand;
import com.ryan_mtg.servobot.commands.game_queue.ShowGameQueueCommand;
import com.ryan_mtg.servobot.commands.ShowValueCommand;
import com.ryan_mtg.servobot.commands.giveaway.StartRaffleCommand;
import com.ryan_mtg.servobot.commands.trigger.Trigger;
import com.ryan_mtg.servobot.commands.trigger.TriggerVisitor;
import com.ryan_mtg.servobot.data.models.CommandRow;
import com.ryan_mtg.servobot.data.models.ReactionCommandRow;
import com.ryan_mtg.servobot.data.models.TriggerRow;
import com.ryan_mtg.servobot.data.repositories.CommandRepository;
import com.ryan_mtg.servobot.commands.hierarchy.Command;
import com.ryan_mtg.servobot.commands.CommandVisitor;
import com.ryan_mtg.servobot.commands.chat.FactsCommand;
import com.ryan_mtg.servobot.commands.chat.MessageChannelCommand;
import com.ryan_mtg.servobot.commands.chat.TextCommand;
import com.ryan_mtg.servobot.commands.TierCommand;
import com.ryan_mtg.servobot.data.repositories.TriggerRepository;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.model.books.Book;
import com.ryan_mtg.servobot.model.reaction.ReactionCommand;
import com.ryan_mtg.servobot.scryfall.ScryfallQuerier;
import com.ryan_mtg.servobot.utility.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.function.Consumer;

@Component
public class CommandSerializer {
    private static Logger LOGGER = LoggerFactory.getLogger(CommandSerializer.class);

    private final CommandRepository commandRepository;
    private final TriggerRepository triggerRepository;
    private final RateLimitSerializer rateLimitSerializer;
    private final ScryfallQuerier scryfallQuerier;

    public CommandSerializer(final CommandRepository commandRepository, final TriggerRepository triggerRepository,
            final RateLimitSerializer rateLimitSerializer, final ScryfallQuerier scryfallQuerier) {
        this.commandRepository = commandRepository;
        this.triggerRepository = triggerRepository;
        this.rateLimitSerializer = rateLimitSerializer;
        this.scryfallQuerier = scryfallQuerier;
    }

    public Command createCommand(final CommandRow commandRow, final Map<Integer, Book> bookMap)
            throws BotErrorException {
        int id = commandRow.getId();

        RateLimit rateLimit = rateLimitSerializer.createRateLimit(commandRow);

        CommandSettings commandSettings =
                new CommandSettings(commandRow.getFlags(), commandRow.getPermission(), rateLimit);
        CommandType commandType = CommandType.getFromValue(commandRow.getType());
        switch (commandType) {
            case ADD_COMMAND_TYPE:
                return new AddCommand(id, commandSettings);
            case ADD_REACTION_COMMAND_TYPE:
                return new AddReactionCommand(id, commandSettings, Strings.trim(commandRow.getStringParameter()));
            case ADD_STATEMENT_COMMAND_TYPE:
                return new AddStatementCommand(id, commandSettings);
            case ARREST_COMMAND_TYPE:
                return new ArrestCommand(id, commandSettings, Strings.trim(commandRow.getStringParameter()),
                        Strings.trim(commandRow.getStringParameter2()));
            case CARD_SEARCH_COMMAND_TYPE:
                return new CardSearchCommand(id, commandSettings, scryfallQuerier);
            case DELAYED_ALERT_COMMAND_TYPE:
                return new DelayedAlertCommand(id, commandSettings, Duration.ofSeconds(commandRow.getLongParameter()),
                        Strings.trim(commandRow.getStringParameter()));
            case DELETE_COMMAND_TYPE:
                return new DeleteCommand(id, commandSettings);
            case ENTER_RAFFLE_COMMAND_TYPE:
                int giveawayId = (int) (long) commandRow.getLongParameter();
                return new EnterRaffleCommand(id, commandSettings, giveawayId,
                        Strings.trim(commandRow.getStringParameter()));
            case EVALUATE_EXPRESSION_COMMAND_TYPE:
                boolean gabyEasterEgg = commandRow.getLongParameter() != null && commandRow.getLongParameter() != 0;
                return new EvaluateExpressionCommand(id, commandSettings, gabyEasterEgg);

            case FACTS_COMMAND_TYPE:
                int bookId = (int) (long) commandRow.getLongParameter();
                return new FactsCommand(id, commandSettings, bookMap.get(bookId));
            case GAME_QUEUE_COMMAND_TYPE:
                int gameQueueId = (int) (long) commandRow.getLongParameter();
                return new GameQueueCommand(id, commandSettings, gameQueueId);
            case JAIL_BREAK_COMMAND_TYPE:
                return new JailBreakCommand(id, commandSettings, Strings.trim(commandRow.getStringParameter()),
                        Strings.trim(commandRow.getStringParameter2()));
            case JAIL_COMMAND_TYPE:
                int jailThreshold = (int) (long) commandRow.getLongParameter();
                return new JailCommand(id, commandSettings, Strings.trim(commandRow.getStringParameter()),
                        jailThreshold, commandRow.getStringParameter2().trim());
            case JAIL_RELEASE_COMMAND_TYPE:
                return new JailReleaseCommand(id, commandSettings, Strings.trim(commandRow.getStringParameter()));
            case JOIN_GAME_QUEUE_COMMAND_TYPE:
                gameQueueId = (int) (long) commandRow.getLongParameter();
                return new JoinGameQueueCommand(id, commandSettings, gameQueueId);
            case MESSAGE_CHANNEL_COMMAND_TYPE:
                return new MessageChannelCommand(id, commandSettings, commandRow.getLongParameter().intValue(),
                        Strings.trim(commandRow.getStringParameter()), Strings.trim(commandRow.getStringParameter2()));
            case RAFFLE_STATUS_COMMAND_TYPE:
                giveawayId = (int) (long) commandRow.getLongParameter();
                return new RaffleStatusCommand(id, commandSettings, giveawayId,
                        Strings.trim(commandRow.getStringParameter()));
            case REMOVE_FROM_GAME_QUEUE_COMMAND_TYPE:
                gameQueueId = (int) (long) commandRow.getLongParameter();
                return new RemoveFromGameQueueCommand(id, commandSettings, gameQueueId);
            case REQUEST_PRIZE_COMMAND_TYPE:
                giveawayId = (int) (long) commandRow.getLongParameter();
                return new RequestPrizeCommand(id, commandSettings, giveawayId);
            case SCRYFALL_SEARCH_COMMAND_TYPE:
                return new ScryfallSearchCommand(id, commandSettings, scryfallQuerier);
            case SELECT_WINNER_COMMAND_TYPE:
                giveawayId = (int) (long) commandRow.getLongParameter();
                return new SelectWinnerCommand(id, commandSettings, giveawayId,
                        Strings.trim(commandRow.getStringParameter()), Strings.trim(commandRow.getStringParameter2()));
            case SET_ARENA_USERNAME_COMMAND_TYPE:
                return new SetArenaUsernameCommand(id, commandSettings);
            case SET_STATUS_COMMAND_TYPE:
                bookId = (int) (long) commandRow.getLongParameter();
                return new SetStatusCommand(id, commandSettings, bookMap.get(bookId));
            case SET_ROLE_COMMAND_TYPE:
                return new SetRoleCommand(id, commandSettings, Strings.trim(commandRow.getStringParameter()));
            case SET_USERS_ROLE_COMMAND_TYPE:
                return new SetUsersRoleCommand(id, commandSettings, Strings.trim(commandRow.getStringParameter()),
                        Strings.trim(commandRow.getStringParameter2()));
            case SET_VALUE_COMMAND_TYPE:
                return new SetValueCommand(id, commandSettings);
            case SHOW_ARENA_USERNAMES_COMMAND_TYPE:
                return new ShowArenaUsernamesCommand(id, commandSettings);
            case SHOW_GAME_QUEUE_COMMAND_TYPE:
                gameQueueId = (int) (long) commandRow.getLongParameter();
                return new ShowGameQueueCommand(id, commandSettings, gameQueueId);
            case SHOW_VALUE_COMMAND_TYPE:
                return new ShowValueCommand(id, commandSettings);
            case START_RAFFLE_COMMAND_TYPE:
                giveawayId = (int) (long) commandRow.getLongParameter();
                return new StartRaffleCommand(id, commandSettings, giveawayId,
                        Strings.trim(commandRow.getStringParameter()));
            case TEXT_COMMAND_TYPE:
                return new TextCommand(id, commandSettings, Strings.trim(commandRow.getStringParameter()));
            case TIER_COMMAND_TYPE:
                return new TierCommand(id, commandSettings);
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
        public void visitArrestCommand(final ArrestCommand arrestCommand) {
            saveCommand(arrestCommand, commandRow -> {
                commandRow.setStringParameter(arrestCommand.getPrisonRole());
                commandRow.setStringParameter2(arrestCommand.getMessage());
            });
        }

        @Override
        public void visitCardSearchCommand(final CardSearchCommand cardSearchCommand) {
            saveCommand(cardSearchCommand, commandRow -> {});
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
                commandRow.setStringParameter(raffleStatusCommand.getResponse());
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
        public void visitJailReleaseCommand(final JailReleaseCommand jailReleaseCommand) {
            saveCommand(jailReleaseCommand, commandRow -> {
                commandRow.setStringParameter(jailReleaseCommand.getPrisonRole());
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
        public void visitScryfallSearchCommand(final ScryfallSearchCommand scryfallSearchCommand) {
            saveCommand(scryfallSearchCommand, commandRow -> {});
        }

        @Override
        public void visitSelectWinnerCommand(final SelectWinnerCommand selectWinnerCommand) {
            saveCommand(selectWinnerCommand, commandRow -> {
                commandRow.setLongParameter(selectWinnerCommand.getGiveawayId());
                commandRow.setStringParameter(selectWinnerCommand.getResponse());
                commandRow.setStringParameter2(selectWinnerCommand.getDiscordChannel());
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
            commandRow.setPermission(command.getPermission());
            rateLimitSerializer.saveRateLimit(commandRow, command.getRateLimit());
            commandRow.setType(command.getType().getType());
            commandRow.setBotHomeId(botHomeId);
            consumer.accept(commandRow);
        }
    }
}
