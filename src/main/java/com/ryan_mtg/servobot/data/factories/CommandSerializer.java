package com.ryan_mtg.servobot.data.factories;

import com.ryan_mtg.servobot.commands.AddCommand;
import com.ryan_mtg.servobot.commands.CommandAlert;
import com.ryan_mtg.servobot.commands.CommandAlias;
import com.ryan_mtg.servobot.commands.CommandEvent;
import com.ryan_mtg.servobot.commands.DeleteCommand;
import com.ryan_mtg.servobot.commands.GameQueueCommand;
import com.ryan_mtg.servobot.commands.JoinGameQueueCommand;
import com.ryan_mtg.servobot.commands.RemoveFromGameQueueCommand;
import com.ryan_mtg.servobot.commands.SetArenaUsernameCommand;
import com.ryan_mtg.servobot.commands.SetRoleCommand;
import com.ryan_mtg.servobot.commands.ShowArenaUsernamesCommand;
import com.ryan_mtg.servobot.commands.ShowGameQueueCommand;
import com.ryan_mtg.servobot.data.models.CommandAlertRow;
import com.ryan_mtg.servobot.data.models.CommandAliasRow;
import com.ryan_mtg.servobot.data.models.CommandEventRow;
import com.ryan_mtg.servobot.data.models.CommandRow;
import com.ryan_mtg.servobot.data.repositories.CommandAlertRepository;
import com.ryan_mtg.servobot.data.repositories.CommandAliasRepository;
import com.ryan_mtg.servobot.data.repositories.CommandEventRepository;
import com.ryan_mtg.servobot.data.repositories.CommandRepository;
import com.ryan_mtg.servobot.commands.Command;
import com.ryan_mtg.servobot.commands.CommandVisitor;
import com.ryan_mtg.servobot.commands.FactsCommand;
import com.ryan_mtg.servobot.commands.MessageChannelCommand;
import com.ryan_mtg.servobot.commands.TextCommand;
import com.ryan_mtg.servobot.commands.TierCommand;
import com.ryan_mtg.servobot.model.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Consumer;

@Component
public class CommandSerializer {
    private static Logger LOGGER = LoggerFactory.getLogger(CommandSerializer.class);

    @Autowired
    private CommandRepository commandRepository;

    @Autowired
    private CommandAliasRepository commandAliasRepository;

    @Autowired
    private CommandEventRepository commandEventRepository;

    @Autowired
    private CommandAlertRepository commandAlertRepository;

    public Command createCommand(final CommandRow commandRow, final Map<Integer, Book> bookMap) {
        int id = commandRow.getId();
        switch (commandRow.getType()) {
            case AddCommand.TYPE:
                return new AddCommand(id, commandRow.isSecure(), commandRow.getPermission());
            case DeleteCommand.TYPE:
                return new DeleteCommand(id, commandRow.isSecure(), commandRow.getPermission());
            case TextCommand.TYPE:
                return new TextCommand(id, commandRow.isSecure(), commandRow.getPermission(),
                                       commandRow.getStringParameter());
            case FactsCommand.TYPE:
                int bookId = (int) (long) commandRow.getLongParameter();
                return new FactsCommand(id, commandRow.isSecure(), commandRow.getPermission(), bookMap.get(bookId));
            case GameQueueCommand.TYPE:
                int gameQueueId = (int) (long) commandRow.getLongParameter();
                return new GameQueueCommand(id, commandRow.isSecure(), commandRow.getPermission(), gameQueueId);
            case JoinGameQueueCommand.TYPE:
                gameQueueId = (int) (long) commandRow.getLongParameter();
                return new JoinGameQueueCommand(id, commandRow.isSecure(), commandRow.getPermission(), gameQueueId);
            case MessageChannelCommand.TYPE:
                return new MessageChannelCommand(id, commandRow.isSecure(), commandRow.getPermission(),
                        commandRow.getLongParameter().intValue(), commandRow.getStringParameter(),
                        commandRow.getStringParameter2());
            case RemoveFromGameQueueCommand.TYPE:
                gameQueueId = (int) (long) commandRow.getLongParameter();
                return new RemoveFromGameQueueCommand(id, commandRow.isSecure(), commandRow.getPermission(),
                        gameQueueId);
            case SetArenaUsernameCommand.TYPE:
                return new SetArenaUsernameCommand(id, commandRow.isSecure(), commandRow.getPermission());
            case ShowArenaUsernamesCommand.TYPE:
                return new ShowArenaUsernamesCommand(id, commandRow.isSecure(), commandRow.getPermission());
            case ShowGameQueueCommand.TYPE:
                gameQueueId = (int) (long) commandRow.getLongParameter();
                return new ShowGameQueueCommand(id, commandRow.isSecure(), commandRow.getPermission(), gameQueueId);
            case TierCommand.TYPE:
                return new TierCommand(id, commandRow.isSecure(), commandRow.getPermission());
        }
        throw new IllegalArgumentException("Unsupported type: " + commandRow.getType());
    }

    public void saveCommand(final int botHomeId, final Command command) {
        CommandSerializationVisitor serializer = new CommandSerializationVisitor(botHomeId);
        command.acceptVisitor(serializer);
        CommandRow commandRow = serializer.getCommandRow();
        commandRepository.save(commandRow);
        command.setId(commandRow.getId());
    }

    public void saveCommandAlias(final int commandId, final CommandAlias commandAlias) {
        CommandAliasRow aliasRow = new CommandAliasRow(commandAlias.getId(), commandId, commandAlias.getAlias());
        commandAliasRepository.save(aliasRow);
        commandAlias.setId(aliasRow.getId());
    }

    public CommandAlias getAlias(final int aliasId) {
        CommandAliasRow commandAliasRow = commandAliasRepository.findById(aliasId).get();
        return new CommandAlias(commandAliasRow.getId(), commandAliasRow.getAlias());
    }

    public CommandEvent getCommandEvent(int eventId) {
        CommandEventRow commandEventRow = commandEventRepository.findById(eventId).get();
        return new CommandEvent(commandEventRow.getId(), commandEventRow.getEventType());
    }

    public CommandAlert getCommandAlert(int alertId) {
        CommandAlertRow commandAlertRow = commandAlertRepository.findById(alertId).get();
        return new CommandAlert(commandAlertRow.getId(), commandAlertRow.getAlertToken());
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
        public void visitDeleteCommand(final DeleteCommand deleteCommand) {
            saveCommand(deleteCommand, commandRow -> {});
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
            commandRow.setSecure(command.isSecure());
            commandRow.setType(command.getType());
            commandRow.setBotHomeId(botHomeId);
            commandRow.setPermission(command.getPermission());
            consumer.accept(commandRow);
        }
    }
}
