package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.commands.hierarchy.Command;
import com.ryan_mtg.servobot.commands.trigger.CommandAlert;
import com.ryan_mtg.servobot.commands.trigger.CommandAlias;
import com.ryan_mtg.servobot.commands.trigger.CommandEvent;
import com.ryan_mtg.servobot.commands.trigger.Trigger;
import com.ryan_mtg.servobot.commands.trigger.TriggerVisitor;
import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.model.alerts.AlertGenerator;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class CommandTable {
    private static Logger LOGGER = LoggerFactory.getLogger(CommandTable.class);

    @Getter
    private final int contextId;
    private final boolean isCaseSensitive;

    private Map<Integer, Command> idToCommandMap = new HashMap<>();

    private Map<Trigger, Command> triggerCommandMap = new IdentityHashMap<>();
    private Map<Command, List<Trigger>> reverseTriggerMap = new HashMap<>();

    @Getter
    private List<Trigger> triggers = new ArrayList<>();

    private Map<String, Command> commandMap = new HashMap<>();
    private Map<String, CommandAlias> aliasMap = new HashMap<>();
    private Map<CommandEvent.Type, List<CommandEvent>> eventMap = new HashMap<>();
    private Map<String, List<CommandAlert>> alertMap = new HashMap<>();

    private List<AlertGenerator> alertGenerators = new ArrayList<>();

    public CommandTable(final int contextId, final boolean isCaseSensitive) {
        this.contextId = contextId;
        this.isCaseSensitive = isCaseSensitive;
    }

    public Set<Command> getCommands() {
        return reverseTriggerMap.keySet();
    }

    public Command getCommand(final Trigger trigger) {
        return triggerCommandMap.get(trigger);
    }

    public void registerCommand(final Command command) {
        idToCommandMap.put(command.getId(), command);
    }

    public CommandTableEdit addCommand(Command command) {
        CommandTableEdit commandTableEdit = new CommandTableEdit();
        commandTableEdit.save(contextId, command, this::registerCommand);
        return commandTableEdit;
    }

    public CommandTableEdit addCommand(final String alias, final Command newCommand) throws UserError {
        CommandTableEdit commandTableEdit = deleteCommand(alias);
        CommandAlias commandAlias = createAlias(newCommand, alias);
        commandTableEdit.save(contextId, newCommand, commandAlias, this::registerCommand);
        return commandTableEdit;
    }

    public CommandTableEdit addAlias(final String newAlias, final String existingAlias) throws UserError {
        if (newAlias.equals(existingAlias)) {
            throw new UserError("Command can't alias itself.");
        }
        CommandTableEdit commandTableEdit = deleteCommand(newAlias);

        Command command = getCommand(existingAlias);

        CommandAlias commandAlias = createAlias(command, newAlias);
        commandTableEdit.save(command.getId(), commandAlias);
        return commandTableEdit;
    }

    public CommandTableEdit deleteCommand(final String alias) {
        return deleteAlias(alias, true);
    }

    public CommandTableEdit deleteCommand(final int commandId) {
        CommandTableEdit commandTableEdit = new CommandTableEdit();

        Command command = idToCommandMap.get(commandId);
        if (command == null) {
            return commandTableEdit;
        }
        idToCommandMap.remove(command.getId());
        commandTableEdit.delete(command);

        if (reverseTriggerMap.containsKey(command)) {
            List<Trigger> triggers = new ArrayList<>(reverseTriggerMap.get(command));

            for (Trigger trigger : triggers) {
                deleteTrigger(trigger, commandTableEdit, false);
            }
        }

        return commandTableEdit;
    }

    public CommandTableEdit addTrigger(final int commandId, final int triggerType, final String text) throws UserError {
        Command command = idToCommandMap.get(commandId);
        return addTrigger(command, triggerType, text);
    }

    public CommandTableEdit addTrigger(final Command command, final int triggerType, final String text)
            throws UserError {
        CommandTableEdit commandTableEdit;
        Trigger trigger;
        switch (triggerType) {
            case CommandAlias.TYPE:
                commandTableEdit = deleteAlias(text, false);
                trigger = new CommandAlias(Trigger.UNREGISTERED_ID, text);
                break;
            case CommandEvent.TYPE:
                commandTableEdit = new CommandTableEdit();
                trigger = new CommandEvent(Trigger.UNREGISTERED_ID, CommandEvent.Type.valueOf(text));
                break;
            case CommandAlert.TYPE:
                commandTableEdit = new CommandTableEdit();
                trigger = new CommandAlert(Trigger.UNREGISTERED_ID, text);
                break;
            default:
                throw new IllegalArgumentException("Unsupported trigger type: " + triggerType);
        }

        commandTableEdit.save(contextId, command, trigger, this::registerCommand);

        return commandTableEdit;
    }

    public CommandTableEdit deleteTrigger(final int triggerId) {
        CommandTableEdit commandTableEdit = new CommandTableEdit();
        Trigger trigger = triggers.stream().filter(t -> t.getId() == triggerId).findFirst().get();
        deleteTrigger(trigger, commandTableEdit, false);
        return commandTableEdit;
    }

    public CommandTableEdit addAlertGenerator(final AlertGenerator alertGenerator) {
        CommandTableEdit commandTableEdit = new CommandTableEdit();
        alertGenerators.add(alertGenerator);
        commandTableEdit.save(contextId, alertGenerator);
        return commandTableEdit;
    }

    public CommandTableEdit deleteAlertGenerator(final int alertGeneratorId) {
        Optional<AlertGenerator> alertGenerator = alertGenerators.stream()
                .filter(ag -> ag.getId() == alertGeneratorId).findFirst();
        CommandTableEdit commandTableEdit = new CommandTableEdit();

        alertGenerator.ifPresent(ag -> {
            commandTableEdit.delete(ag);
            alertGenerators.remove(ag);
        });

        return commandTableEdit;
    }

    public void registerCommand(final Command command, final Trigger trigger) {
        registerCommand(command);
        triggerCommandMap.put(trigger, command);
        reverseTriggerMap.computeIfAbsent(command, newCommand -> new ArrayList<>()).add(trigger);
        triggers.add(trigger);
        trigger.acceptVisitor(new RegisterTriggerVisitor(command));
    }

    public void addAlertGenerators(final List<AlertGenerator> alertGenerators) {
        this.alertGenerators.addAll(alertGenerators);
    }

    public List<AlertGenerator> getAlertGenerators() {
        return alertGenerators;
    }

    public CommandMapping getCommandMapping() {
        return new CommandMapping(idToCommandMap, triggerCommandMap);
    }

    public Command getCommand(final String token) {
        return commandMap.get(canonicalize(token));
    }

    public boolean hasCommand(final String token) {
        return commandMap.containsKey(canonicalize(token));
    }

    public <CommandType extends Command> List<CommandType> getCommands(final CommandEvent.Type eventType,
                                                       final Class<CommandType> commandClass) {
        List<CommandEvent> events = eventMap.get(eventType);
        if (events != null) {
            return events.stream().map(event -> triggerCommandMap.get(event)).filter(commandClass::isInstance)
                    .map(commandClass::cast).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    public <CommandType extends Command> List<CommandType> getCommandsFromAlertToken(final String alertToken,
                                                       final Class<CommandType> commandClass) {
        List<CommandAlert> alerts = alertMap.get(alertToken);
        if (alerts != null) {
            return alerts.stream().map(alert -> triggerCommandMap.get(alert)).filter(commandClass::isInstance)
                    .map(commandClass::cast).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    public void setTimeZone(final String timeZone) {
        for (AlertGenerator alertGenerator : alertGenerators) {
            alertGenerator.setTimeZone(timeZone);
        }
    }

    public Command secureCommand(final int commandId, final boolean secure) {
        Command command = idToCommandMap.get(commandId);
        command.setSecure(secure);
        return command;
    }

    public Command getCommand(final int commandId) {
        return idToCommandMap.get(commandId);
    }

    private String canonicalize(final String token) {
        return isCaseSensitive ? token : token.toLowerCase();
    }

    private CommandAlias createAlias(final Command newCommand, final String text) throws UserError {
        String canonicalAlias=canonicalize(text);
        commandMap.put(canonicalAlias, newCommand);
        CommandAlias commandAlias = new CommandAlias(CommandAlias.UNREGISTERED_ID, text);
        aliasMap.put(canonicalAlias, commandAlias);
        return commandAlias;
    }

    private void deleteTrigger(final Trigger trigger, final CommandTableEdit commandTableEdit,
                               final boolean deleteUnreferencedCommand) {
        Command command = triggerCommandMap.get(trigger);
        trigger.acceptVisitor(new TriggerDeleteVisitor());

        triggers.remove(trigger);
        triggerCommandMap.remove(trigger);
        removeMappedElement(reverseTriggerMap, command, trigger);

        commandTableEdit.delete(trigger);

        if (!reverseTriggerMap.containsKey(command) && deleteUnreferencedCommand) {
            commandTableEdit.delete(command);
            idToCommandMap.remove(command.getId());
        }
    }

    private CommandTableEdit deleteAlias(final String alias, final boolean deleteUnreferencedCommand) {
        CommandTableEdit commandTableEdit = new CommandTableEdit();
        String canonicalAlias=canonicalize(alias);

        if (aliasMap.containsKey(canonicalAlias)) {
            CommandAlias commandAlias = aliasMap.get(canonicalAlias);
            deleteTrigger(commandAlias, commandTableEdit, deleteUnreferencedCommand);
        }

        return commandTableEdit;
    }

    private class TriggerDeleteVisitor implements TriggerVisitor {
        @Override
        public void visitCommandAlias(final CommandAlias commandAlias) {
            String canonicalAlias = canonicalize(commandAlias.getAlias());
            commandMap.remove(canonicalAlias);
            aliasMap.remove(canonicalAlias);
        }

        @Override
        public void visitCommandEvent(final CommandEvent commandEvent) {
            removeMappedElement(eventMap, commandEvent.getEventType(), commandEvent);
        }

        @Override
        public void visitCommandAlert(final CommandAlert commandAlert) {
            removeMappedElement(alertMap, commandAlert.getAlertToken(), commandAlert);
        }
    }

    private <KeyType, ElementType> void removeMappedElement(final Map<KeyType, List<ElementType>> map,
                                                            final KeyType key, final ElementType element) {
        List<ElementType> mappedElements = map.get(key);
        mappedElements.remove(element);
        if (mappedElements.isEmpty()) {
            map.remove(key);
        }
    }

    private class RegisterTriggerVisitor implements TriggerVisitor {
        private Command command;

        public RegisterTriggerVisitor(final Command command) {
            this.command = command;
        }

        @Override
        public void visitCommandAlias(final CommandAlias commandAlias) {
            String canonicalAlias = canonicalize(commandAlias.getAlias());

            if (commandMap.containsKey(canonicalAlias)) {
                if (aliasMap.get(canonicalAlias) == commandAlias) {
                    return;
                }
                LOGGER.warn("Command " + canonicalAlias + " is already registered");
                throw new IllegalStateException("Command " + canonicalAlias + " is already registered");
            } else {
                LOGGER.trace("Registering alias " + canonicalAlias + " has keys: " + commandMap.keySet().toString());
            }

            commandMap.put(canonicalAlias, command);
            aliasMap.put(canonicalAlias, commandAlias);
        }

        @Override
        public void visitCommandEvent(final CommandEvent commandEvent) {
            eventMap.computeIfAbsent(commandEvent.getEventType(), type -> new ArrayList<>()).add(commandEvent);
        }

        @Override
        public void visitCommandAlert(final CommandAlert commandAlert) {
            alertMap.computeIfAbsent(commandAlert.getAlertToken(), type -> new ArrayList<>()).add(commandAlert);
        }
    }
}
