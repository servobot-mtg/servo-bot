package com.ryan_mtg.servobot.data.factories;

import com.ryan_mtg.servobot.data.models.CommandRow;
import com.ryan_mtg.servobot.data.repositories.CommandRepository;
import com.ryan_mtg.servobot.commands.Command;
import com.ryan_mtg.servobot.commands.CommandVisitor;
import com.ryan_mtg.servobot.commands.FactsCommand;
import com.ryan_mtg.servobot.commands.MessageChannelCommand;
import com.ryan_mtg.servobot.commands.TextCommand;
import com.ryan_mtg.servobot.commands.TierCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class CommandSerializer {
    @Autowired
    private CommandRepository commandRepository;

    public Command createCommand(final CommandRow commandRow) {
        int id = commandRow.getId();
        switch (commandRow.getType()) {
            case TextCommand.TYPE:
                return new TextCommand(id, commandRow.isSecure(), commandRow.getStringParameter());
            case FactsCommand.TYPE:
                return new FactsCommand(id, commandRow.isSecure(), commandRow.getStringParameter());
            case TierCommand.TYPE:
                return new TierCommand(id, commandRow.isSecure());
            case MessageChannelCommand.TYPE:
                return new MessageChannelCommand(id, commandRow.isSecure(), commandRow.getLongParameter().intValue(),
                        commandRow.getStringParameter(), commandRow.getStringParameter2());
        }
        throw new IllegalArgumentException("Unsupported type: " + commandRow.getType());
    }

    public CommandRow saveCommand(final int botHomeId, final Command command) {
        CommandSerializationVisitor serializer = new CommandSerializationVisitor(commandRepository, botHomeId);
        command.acceptVisitor(serializer);
        return serializer.getCommandRow();
    }

    private class CommandSerializationVisitor implements CommandVisitor {
        private final CommandRepository commandRepository;
        private final int botHomeId;
        private final CommandRow commandRow = new CommandRow();

        public CommandSerializationVisitor(final CommandRepository commandRepository, final  int botHomeId) {
            this.commandRepository = commandRepository;
            this.botHomeId = botHomeId;
        }

        public CommandRow getCommandRow() {
            return commandRow;
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
            consumer.accept(commandRow);
        }
    }
}
