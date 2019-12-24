package com.ryan_mtg.servobot.commands;

import com.google.common.collect.ImmutableMap;
import com.ryan_mtg.servobot.model.alerts.AlertGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CommandTable {
    private static Logger LOGGER = LoggerFactory.getLogger(CommandTable.class);

    private boolean isCaseSensitive;

    private Map<Integer, Command> idToCommandMap = new HashMap<>();

    private Map<String, MessageCommand> commandMap = new HashMap<>();
    private Map<String, CommandAlias> aliasMap = new HashMap<>();
    private Map<CommandAlias, MessageCommand> aliasCommandMap = new HashMap<>();
    private Map<MessageCommand, List<CommandAlias>> reverseAliasMap = new HashMap<>();

    private List<CommandEvent> events = new ArrayList<>();
    private Map<CommandEvent, Command> eventCommandMap = new HashMap<>();
    private Map<CommandEvent.Type, List<CommandEvent>> eventMap = new HashMap<>();
    private Map<Command, List<CommandEvent>> reverseEventMap = new HashMap<>();

    private List<CommandAlert> alerts = new ArrayList<>();
    private Map<CommandAlert, HomeCommand> alertCommandMap = new HashMap<>();
    private Map<String, List<CommandAlert>> alertMap = new HashMap<>();
    private Map<HomeCommand, List<CommandAlert>> reverseAlertMap = new HashMap<>();

    private List<AlertGenerator> alertGenerators;

    public CommandTable(final boolean isCaseSensitive) {
        this.isCaseSensitive = isCaseSensitive;
    }

    public void registerCommand(final Command command) {
        idToCommandMap.put(command.getId(), command);
    }

    public void registerCommand(final MessageCommand command, final CommandAlias commandAlias) {
        registerCommand(command);
        String alias = canonicalize(commandAlias.getAlias());
        if (commandMap.containsKey(alias)) {
            LOGGER.warn("Command " + alias + " is already registered");
            throw new IllegalStateException("Command " + alias + " is already registered");
        } else {
            LOGGER.trace("Registering alias " + alias + " has keys: " + commandMap.keySet().toString());
        }
        commandMap.put(alias, command);
        aliasMap.put(alias, commandAlias);
        aliasCommandMap.put(commandAlias, command);
        reverseAliasMap.computeIfAbsent(command, newCommand -> new ArrayList<>()).add(commandAlias);
    }

    public CommandTableEdit addCommand(final String alias, final MessageCommand newCommand) {
        CommandTableEdit commandTableEdit = deleteCommand(alias);
        CommandAlias commandAlias = createAlias(newCommand, alias);
        commandTableEdit.save(newCommand, commandAlias, this::registerCommand, this::aliasSaved);
        return commandTableEdit;
    }

    public CommandTableEdit deleteCommand(final String alias) {
        return deleteAlias(alias, true);
    }

    private CommandTableEdit deleteAlias(final String alias, final boolean deleteUnreferencedCommand) {
        CommandTableEdit commandTableEdit = new CommandTableEdit();
        String canonicalAlias=canonicalize(alias);

        if (aliasMap.containsKey(canonicalAlias)) {
            MessageCommand command = commandMap.get(canonicalAlias);
            CommandAlias commandAlias = aliasMap.get(canonicalAlias);
            List<CommandAlias> oldCommandsAliases = reverseAliasMap.get(command);

            oldCommandsAliases.remove(commandAlias);
            if (oldCommandsAliases.isEmpty() && deleteUnreferencedCommand) {
                commandTableEdit.delete(command);
                reverseAliasMap.remove(command);

                idToCommandMap.remove(command.getId());
            }

            aliasCommandMap.remove(commandAlias);
            aliasMap.remove(canonicalAlias);
            commandMap.remove(canonicalAlias);
            commandTableEdit.delete(commandAlias);
        }

        return commandTableEdit;
    }

    public CommandTableEdit deleteCommand(final int commandId) {
        CommandTableEdit commandTableEdit = new CommandTableEdit();

        Command command = idToCommandMap.get(commandId);
        idToCommandMap.remove(command.getId());
        commandTableEdit.delete(command);

        if (command instanceof MessageCommand) {
            MessageCommand messageCommand = (MessageCommand) command;

            if (reverseAliasMap.containsKey(messageCommand)) {
                List<CommandAlias> aliases = new ArrayList<>(reverseAliasMap.get(messageCommand));

                for (CommandAlias commandAlias : aliases) {
                    deleteAlias(commandAlias, commandTableEdit);
                }
            }
        }

        if (reverseEventMap.containsKey(command)) {
            List<CommandEvent> events = new ArrayList<>(reverseEventMap.get(command));

            for (CommandEvent commandEvent : events) {
                deleteEvent(commandEvent, commandTableEdit);
            }
        }

        if (command instanceof HomeCommand) {
            HomeCommand homeCommand = (HomeCommand) command;

            if (reverseAlertMap.containsKey(homeCommand)) {
                List<CommandAlert> alerts = new ArrayList<>(reverseAlertMap.get(homeCommand));

                for (CommandAlert commandAlert : alerts) {
                    deleteAlert(commandAlert, commandTableEdit);
                }
            }
        }

        return commandTableEdit;
    }

    public CommandTableEdit addTrigger(final int commandId, final int triggerType, final String text) {
        MessageCommand newCommand = (MessageCommand) idToCommandMap.get(commandId);

        CommandTableEdit commandTableEdit = deleteAlias(text, false);
        CommandAlias commandAlias = createAlias(newCommand, text);
        commandTableEdit.save(commandId, commandAlias, this::aliasSaved);
        return commandTableEdit;

    }

    public CommandTableEdit deleteAlias(final CommandAlias commandAlias) {
        CommandTableEdit commandTableEdit = new CommandTableEdit();
        deleteAlias(commandAlias, commandTableEdit);
        return commandTableEdit;
    }

    public CommandTableEdit deleteEvent(CommandEvent commandEvent) {
        CommandTableEdit commandTableEdit = new CommandTableEdit();
        deleteEvent(commandEvent, commandTableEdit);
        return commandTableEdit;
    }

    public CommandTableEdit deleteAlert(CommandAlert commandAlert) {
        CommandTableEdit commandTableEdit = new CommandTableEdit();
        deleteAlert(commandAlert, commandTableEdit);
        return commandTableEdit;
    }

    public void registerCommand(final Command command, final CommandEvent commandEvent) {
        registerCommand(command);
        eventCommandMap.put(commandEvent, command);
        reverseEventMap.computeIfAbsent(command, newCommand -> new ArrayList<>()).add(commandEvent);
        eventMap.computeIfAbsent(commandEvent.getEventType(), type -> new ArrayList<>()).add(commandEvent);
        events.add(commandEvent);
    }

    public void registerCommand(final HomeCommand homeCommand, final CommandAlert commandAlert) {
        registerCommand(homeCommand);
        alertCommandMap.put(commandAlert, homeCommand);
        reverseAlertMap.computeIfAbsent(homeCommand, command -> new ArrayList<>()).add(commandAlert);
        alertMap.computeIfAbsent(commandAlert.getAlertToken(), type -> new ArrayList<>()).add(commandAlert);
        alerts.add(commandAlert);
    }

    public Collection<CommandAlias> getAliases() {
        return aliasMap.values();
    }

    public List<CommandEvent> getEvents() {
        return events;
    }

    public void setAlertGenerators(final List<AlertGenerator> alertGenerators) {
        this.alertGenerators = alertGenerators;
    }

    public List<AlertGenerator> getAlertGenerators() {
        return alertGenerators;
    }

    public Map<String, MessageCommand> getCommandList() {
        return ImmutableMap.copyOf(commandMap);
    }

    public CommandMapping getCommandMapping() {
        return new CommandMapping(idToCommandMap, aliasCommandMap, eventCommandMap, alertCommandMap);
    }

    public MessageCommand getCommand(final String token) {
        return commandMap.get(canonicalize(token));
    }

    public <CommandType extends Command> List<CommandType> getCommands(final CommandEvent.Type eventType,
                                                       final Class<CommandType> commandClass) {
        List<CommandEvent> events = eventMap.get(eventType);
        if (events != null) {
            return events.stream().map(event -> getCommand(event)).filter(command -> commandClass.isInstance(command))
                    .map(command -> commandClass.cast(command)).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    public Command getCommand(final CommandEvent event) {
        return eventCommandMap.get(event);
    }

    public List<HomeCommand> getCommandsFromAlertToken(final String alertToken) {
        List<CommandAlert> alerts = alertMap.get(alertToken);
        if (alerts != null) {
            return alerts.stream().map(alert -> getCommand(alert)).collect(Collectors.toList());
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

    public Command setCommandPermission(final int commandId, final Permission permission) {
        Command command = idToCommandMap.get(commandId);
        command.setPermission(permission);
        return command;
    }

    private HomeCommand getCommand(final CommandAlert alert) {
        return alertCommandMap.get(alert);
    }

    private String canonicalize(final String token) {
        return isCaseSensitive ? token : token.toLowerCase();
    }

    private CommandAlias createAlias(final MessageCommand newCommand, final String text) {
        String canonicalAlias=canonicalize(text);
        commandMap.put(canonicalAlias, newCommand);
        CommandAlias commandAlias = new CommandAlias(CommandAlias.UNREGISTERED_ID, text);
        aliasMap.put(canonicalAlias, commandAlias);
        reverseAliasMap.computeIfAbsent(newCommand, command -> new ArrayList<>()).add(commandAlias);
        return commandAlias;
    }

    private void aliasSaved(final int commandId, final CommandAlias commandAlias) {
        MessageCommand command = (MessageCommand) idToCommandMap.get(commandId);
        aliasCommandMap.put(commandAlias, command);
    }

    private void deleteAlias(final CommandAlias commandAlias, final CommandTableEdit commandTableEdit) {
        String canonicalAlias = canonicalize(commandAlias.getAlias());
        MessageCommand messageCommand = aliasCommandMap.get(commandAlias);

        commandMap.remove(canonicalAlias);
        aliasMap.remove(canonicalAlias);
        aliasCommandMap.remove(commandAlias);
        removeMappedElement(reverseAliasMap, messageCommand, commandAlias);

        commandTableEdit.delete(commandAlias);
    }

    private void deleteEvent(final CommandEvent commandEvent, final CommandTableEdit commandTableEdit) {
        Command command = eventCommandMap.get(commandEvent);

        events.remove(commandEvent);
        eventCommandMap.remove(commandEvent);
        removeMappedElement(eventMap, commandEvent.getEventType(), commandEvent);
        removeMappedElement(reverseEventMap, command, commandEvent);

        commandTableEdit.delete(commandEvent);
    }

    private void deleteAlert(final CommandAlert commandAlert, final CommandTableEdit commandTableEdit) {
        HomeCommand homeCommand = alertCommandMap.get(commandAlert);

        alerts.remove(commandAlert);
        alertCommandMap.remove(commandAlert);

        removeMappedElement(alertMap, commandAlert.getAlertToken(), commandAlert);
        removeMappedElement(reverseAlertMap, homeCommand, commandAlert);

        commandTableEdit.delete(commandAlert);
    }

    private <KeyType, ElementType> void removeMappedElement(final Map<KeyType, List<ElementType>> map,
                                                            final KeyType key, final ElementType element) {
        List<ElementType> mappedElements = map.get(key);
        mappedElements.remove(element);
        if (mappedElements.isEmpty()) {
            eventMap.remove(key);
        }
    }
}
