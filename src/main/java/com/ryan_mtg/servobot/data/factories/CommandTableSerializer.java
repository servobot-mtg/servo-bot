package com.ryan_mtg.servobot.data.factories;

import com.ryan_mtg.servobot.commands.CommandTableEdit;
import com.ryan_mtg.servobot.commands.Trigger;
import com.ryan_mtg.servobot.data.models.AlertGeneratorRow;
import com.ryan_mtg.servobot.data.models.CommandRow;
import com.ryan_mtg.servobot.data.models.TriggerRow;
import com.ryan_mtg.servobot.data.repositories.AlertGeneratorRepository;
import com.ryan_mtg.servobot.data.repositories.CommandRepository;
import com.ryan_mtg.servobot.commands.Command;
import com.ryan_mtg.servobot.commands.CommandTable;
import com.ryan_mtg.servobot.commands.MessageCommand;
import com.ryan_mtg.servobot.data.repositories.TriggerRepository;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.model.Book;
import com.ryan_mtg.servobot.model.alerts.AlertGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class CommandTableSerializer {
    private static Logger LOGGER = LoggerFactory.getLogger(CommandTableSerializer.class);

    @Autowired
    private AlertGeneratorRepository alertGeneratorRepository;

    @Autowired
    private AlertGeneratorSerializer alertGeneratorSerializer;

    @Autowired
    private CommandRepository commandRepository;

    @Autowired
    private CommandSerializer commandSerializer;

    @Autowired
    private TriggerRepository triggerRepository;

    public CommandTable createCommandTable(final int botHomeId, final Map<Integer, Book> bookMap)
            throws BotErrorException  {
        CommandTable commandTable = new CommandTable(false);
        Iterable<CommandRow> commandRows = commandRepository.findAllByBotHomeId(botHomeId);
        Iterable<Integer> commandIds = SerializationSupport.getIds(commandRows, commandRow -> commandRow.getId());

        Map<Integer, List<TriggerRow>> triggerRowMap = SerializationSupport.getIdMapping(
            triggerRepository.findAllByCommandIdIn(commandIds), commandIds, triggerRow -> triggerRow.getCommandId());

        for (CommandRow commandRow : commandRows) {
            Command command = commandSerializer.createCommand(commandRow, bookMap);

            commandTable.registerCommand(command);

            Iterable<TriggerRow> triggerRows = triggerRowMap.get(commandRow.getId());
            for (TriggerRow triggerRow : triggerRows) {
                commandTable.registerCommand(command, commandSerializer.createTrigger(triggerRow));
            }
        }

        Iterable<AlertGeneratorRow> alertGeneratorRows = alertGeneratorRepository.findByBotHomeId(botHomeId);

        List<AlertGenerator> alertGenerators = new ArrayList<>();
        for (AlertGeneratorRow alertGeneratorRow : alertGeneratorRows) {
            alertGenerators.add(alertGeneratorSerializer.createAlertGenerator(alertGeneratorRow));
        }
        commandTable.addAlertGenerators(alertGenerators);

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

    @Transactional(rollbackOn = BotErrorException.class)
    public void commit(final int botHomeId, final CommandTableEdit commandTableEdit) {
        for (Trigger trigger : commandTableEdit.getDeletedTriggers()) {
            triggerRepository.deleteById(trigger.getId());
        }

        for (Command command : commandTableEdit.getDeletedCommands()) {
            commandRepository.deleteById(command.getId());
        }

        for (AlertGenerator alertGenerator : commandTableEdit.getDeletedAlertGenerators()) {
            alertGeneratorRepository.deleteById(alertGenerator.getId());
        }

        for (Command command : commandTableEdit.getSavedCommands()) {
            commandSerializer.saveCommand(botHomeId, command);
            commandTableEdit.commandSaved(command);
        }

        for (Map.Entry<Trigger, Integer> triggerEntry : commandTableEdit.getSavedTriggers().entrySet()) {
            commandSerializer.saveTrigger(triggerEntry.getValue(), triggerEntry.getKey());
            commandTableEdit.triggerSaved(triggerEntry.getKey());
        }

        for (AlertGenerator alertGenerator : commandTableEdit.getSavedAlertGenerators()) {
            alertGeneratorSerializer.saveAlertGenerator(botHomeId, alertGenerator);
        }
    }
}
