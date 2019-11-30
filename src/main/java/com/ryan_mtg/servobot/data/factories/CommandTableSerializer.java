package com.ryan_mtg.servobot.data.factories;

import com.ryan_mtg.servobot.commands.CommandAlert;
import com.ryan_mtg.servobot.commands.CommandTableEdit;
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

import java.util.Collection;
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

            Iterable<CommandAliasRow> aliases = commandAliasRepository.findAllByCommandId(commandRow.getId());
            if (aliases.iterator().hasNext()) {
                MessageCommand messageCommand = (MessageCommand) command;
                for (CommandAliasRow alias : aliases) {
                    commandTable.registerCommand(messageCommand, new CommandAlias(alias.getId(), alias.getAlias()));
                }
            }

            Iterable<CommandEventRow> events = commandEventRepository.findAllByCommandId(commandRow.getId());
            for (CommandEventRow event : events) {
                HomeCommand homeCommand = (HomeCommand) command;
                commandTable.registerCommand(homeCommand, new CommandEvent(event.getId(), event.getEventType()));
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
        Collection<CommandAlias> aliases = commandTable.getAliases();
        Set<MessageCommand> aliasedCommands = new HashSet<>();

        for(CommandAlias alias : aliases) {
            MessageCommand command = commandTable.getCommand(alias.getAlias());
            aliasedCommands.add(command);
        }

        for(MessageCommand command : aliasedCommands) {
            commandSerializer.saveCommand(botHomeId, command);
        }

        for(CommandAlias alias : aliases) {
            Command command = commandTable.getCommand(alias.getAlias());
            commandSerializer.saveCommandAlias(command.getId(), alias);
        }

        Set<HomeCommand> triggeredCommands = new HashSet<>();

        List<CommandEvent> events = commandTable.getEvents();
        for (CommandEvent event : events) {
            triggeredCommands.addAll(commandTable.getCommands(event.getEventType()));
        }

        for(HomeCommand command : triggeredCommands) {
            commandSerializer.saveCommand(botHomeId, command);
        }

        for (CommandEvent event : events) {
            HomeCommand command = commandTable.getCommand(event);
            CommandEventRow eventRow = new CommandEventRow(event.getId(), command.getId(), event.getEventType());
            commandEventRepository.save(eventRow);
        }
    }

    public void commit(final int botHomeId, final CommandTableEdit commandTableEdit) {
        for (CommandAlias commandAlias : commandTableEdit.getDeletedAliases()) {
            commandAliasRepository.deleteById(commandAlias.getId());
        }

        for (MessageCommand messageCommand : commandTableEdit.getDeletedCommands()) {
            commandRepository.deleteById(messageCommand.getId());
        }

        for (MessageCommand messageCommand : commandTableEdit.getSavedCommands()) {
            commandSerializer.saveCommand(botHomeId, messageCommand);
            commandTableEdit.commandSaved(messageCommand);
            CommandAlias commandAlias = commandTableEdit.getSavedAlias(messageCommand);
            commandSerializer.saveCommandAlias(messageCommand.getId(), commandAlias);
        }
    }
}
