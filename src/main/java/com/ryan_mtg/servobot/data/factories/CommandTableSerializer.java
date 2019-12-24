package com.ryan_mtg.servobot.data.factories;

import com.ryan_mtg.servobot.commands.CommandAlert;
import com.ryan_mtg.servobot.commands.CommandTableEdit;
import com.ryan_mtg.servobot.commands.Trigger;
import com.ryan_mtg.servobot.commands.TriggerVisitor;
import com.ryan_mtg.servobot.data.models.AlertGeneratorRow;
import com.ryan_mtg.servobot.data.models.CommandAlertRow;
import com.ryan_mtg.servobot.data.models.CommandAliasRow;
import com.ryan_mtg.servobot.data.models.CommandEventRow;
import com.ryan_mtg.servobot.data.models.CommandRow;
import com.ryan_mtg.servobot.data.repositories.AlertGeneratorRepository;
import com.ryan_mtg.servobot.data.repositories.CommandAlertRepository;
import com.ryan_mtg.servobot.data.repositories.CommandAliasRepository;
import com.ryan_mtg.servobot.data.repositories.CommandEventRepository;
import com.ryan_mtg.servobot.data.repositories.CommandRepository;
import com.ryan_mtg.servobot.commands.Command;
import com.ryan_mtg.servobot.commands.CommandAlias;
import com.ryan_mtg.servobot.commands.CommandEvent;
import com.ryan_mtg.servobot.commands.CommandTable;
import com.ryan_mtg.servobot.commands.HomeCommand;
import com.ryan_mtg.servobot.commands.MessageCommand;
import com.ryan_mtg.servobot.model.Book;
import com.ryan_mtg.servobot.model.alerts.AlertGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class CommandTableSerializer {
    private static Logger LOGGER = LoggerFactory.getLogger(CommandTableSerializer.class);

    @Autowired
    private CommandRepository commandRepository;

    @Autowired
    private CommandSerializer commandSerializer;

    @Autowired
    private AlertGeneratorSerializer alertGeneratorSerializer;

    @Autowired
    private CommandAliasRepository commandAliasRepository;

    @Autowired
    private CommandEventRepository commandEventRepository;

    @Autowired
    private CommandAlertRepository commandAlertRepository;

    @Autowired
    private AlertGeneratorRepository alertGeneratorRepository;

    public CommandTable createCommandTable(final int botHomeId, final Map<Integer, Book> bookMap) {
        Iterable<CommandRow> commandRows = commandRepository.findAllByBotHomeId(botHomeId);
        CommandTable commandTable = new CommandTable(false);
        Set<String> alertTokens = new HashSet<>();

        for (CommandRow commandRow : commandRows) {
            Command command = commandSerializer.createCommand(commandRow, bookMap);

            commandTable.registerCommand(command);

            Iterable<CommandAliasRow> aliases = commandAliasRepository.findAllByCommandId(commandRow.getId());
            if (aliases.iterator().hasNext()) {
                MessageCommand messageCommand = (MessageCommand) command;
                for (CommandAliasRow alias : aliases) {
                    commandTable.registerCommand(messageCommand, new CommandAlias(alias.getId(), alias.getAlias()));
                }
            }

            Iterable<CommandEventRow> events = commandEventRepository.findAllByCommandId(commandRow.getId());
            for (CommandEventRow event : events) {
                commandTable.registerCommand(command, new CommandEvent(event.getId(), event.getEventType()));
            }

            Iterable<CommandAlertRow> alerts = commandAlertRepository.findAllByCommandId(commandRow.getId());
            for (CommandAlertRow alert : alerts) {
                HomeCommand homeCommand = (HomeCommand) command;
                String alertToken = alert.getAlertToken();
                commandTable.registerCommand(homeCommand, new CommandAlert(alert.getId(), alert.getAlertToken()));
                alertTokens.add(alertToken);
            }
        }

        Iterable<AlertGeneratorRow> alertGeneratorRows = alertGeneratorRepository.findByBotHomeId(botHomeId);

        List<AlertGenerator> alertGenerators = StreamSupport.stream(alertGeneratorRows.spliterator(), true)
                .map(alertGeneratorRow -> alertGeneratorSerializer.createAlertGenerator(alertGeneratorRow))
                .collect(Collectors.toList());

        commandTable.setAlertGenerators(alertGenerators);

        return commandTable;
    }

    public void saveCommandTable(final CommandTable commandTable, final int botHomeId) {
        Set<MessageCommand> aliasedCommands = new HashSet<>();

        for(Command command : commandTable.getCommands()) {
            commandSerializer.saveCommand(botHomeId, command);
        }

        for(Trigger trigger : commandTable.getTriggers()) {
            Command command = commandTable.getCommand(trigger);
            commandSerializer.saveTrigger(command.getId(), trigger);
        }
    }

    public void commit(final int botHomeId, final CommandTableEdit commandTableEdit) {
        TriggerDeletionVisitor triggerDeletionVisitor = new TriggerDeletionVisitor();
        for (Trigger trigger : commandTableEdit.getDeletedTriggers()) {
            trigger.acceptVisitor(triggerDeletionVisitor);
        }

        for (Command command : commandTableEdit.getDeletedCommands()) {
            commandRepository.deleteById(command.getId());
        }

        for (Command command : commandTableEdit.getSavedCommands()) {
            commandSerializer.saveCommand(botHomeId, command);
            commandTableEdit.commandSaved(command);
        }

        for (Map.Entry<Trigger, Integer> aliasEntry : commandTableEdit.getSavedTriggers().entrySet()) {
            commandSerializer.saveTrigger(aliasEntry.getValue(), aliasEntry.getKey());
            commandTableEdit.triggerSaved(aliasEntry.getKey());
        }
    }

    private class TriggerDeletionVisitor implements TriggerVisitor {
        @Override
        public void visitCommandAlias(final CommandAlias commandAlias) {
            commandAliasRepository.deleteById(commandAlias.getId());
        }

        @Override
        public void visitCommandEvent(final CommandEvent commandEvent) {
            commandEventRepository.deleteById(commandEvent.getId());
        }

        @Override
        public void visitCommandAlert(final CommandAlert commandAlert) {
            commandAlertRepository.deleteById(commandAlert.getId());
        }
    }
}
