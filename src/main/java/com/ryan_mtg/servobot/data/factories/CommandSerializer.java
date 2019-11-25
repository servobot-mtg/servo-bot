package com.ryan_mtg.servobot.data.factories;

import com.ryan_mtg.servobot.commands.AddCommand;
import com.ryan_mtg.servobot.commands.CommandAlias;
import com.ryan_mtg.servobot.data.models.CommandAliasRow;
import com.ryan_mtg.servobot.data.models.CommandRow;
import com.ryan_mtg.servobot.data.repositories.CommandAliasRepository;
import com.ryan_mtg.servobot.data.repositories.CommandRepository;
import com.ryan_mtg.servobot.commands.Command;
import com.ryan_mtg.servobot.commands.CommandVisitor;
import com.ryan_mtg.servobot.commands.FactsCommand;
import com.ryan_mtg.servobot.commands.MessageChannelCommand;
import com.ryan_mtg.servobot.commands.TextCommand;
import com.ryan_mtg.servobot.commands.TierCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.function.Consumer;

@Component
public class CommandSerializer {
    private static Logger LOGGER = LoggerFactory.getLogger(CommandSerializer.class);

    @Autowired
    private CommandRepository commandRepository;

    @Autowired
    private CommandAliasRepository commandAliasRepository;

    private Random random = new Random();

    public Command createCommand(final CommandRow commandRow) {
        int id = commandRow.getId();
        switch (commandRow.getType()) {
            case TextCommand.TYPE:
                return new TextCommand(id, commandRow.isSecure(), commandRow.getPermission(),
                                       commandRow.getStringParameter());
            case FactsCommand.TYPE:
                return new FactsCommand(id, commandRow.isSecure(), commandRow.getPermission(),
                                        commandRow.getStringParameter(), random);
            case TierCommand.TYPE:
                return new TierCommand(id, commandRow.isSecure(), commandRow.getPermission());
            case MessageChannelCommand.TYPE:
                return new MessageChannelCommand(id, commandRow.isSecure(), commandRow.getPermission(),
                        commandRow.getLongParameter().intValue(), commandRow.getStringParameter(),
                        commandRow.getStringParameter2());
            case AddCommand.TYPE:
                return new AddCommand(id, commandRow.isSecure(), commandRow.getPermission());
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
        public void visitFactsCommand(final FactsCommand factsCommand) {
            saveCommand(factsCommand, commandRow -> {
                commandRow.setStringParameter(factsCommand.getFileName());
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
