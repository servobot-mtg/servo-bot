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
    private Map<MessageCommand, List<CommandAlias>> reverseAliasMap = new HashMap<>();

    private List<CommandEvent> events = new ArrayList<>();
    private Map<CommandEvent, HomeCommand> eventCommandMap = new HashMap<>();
    private Map<CommandEvent.Type, List<CommandEvent>> eventMap = new HashMap<>();

    private List<CommandAlert> alerts = new ArrayList<>();
    private Map<CommandAlert, HomeCommand> alertCommandMap = new HashMap<>();
    private Map<String, List<CommandAlert>> alertMap = new HashMap<>();

    private List<AlertGenerator> alertGenerators;

    public CommandTable(final boolean isCaseSensitive) {
        this.isCaseSensitive = isCaseSensitive;
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
        reverseAliasMap.computeIfAbsent(command, newCommand -> new ArrayList<>()).add(commandAlias);
    }

    public CommandTableEdit addCommand(final String alias, final MessageCommand newCommand) {
        CommandTableEdit commandTableEdit = deleteCommand(alias);
        String canonicalAlias=canonicalize(alias);

        commandMap.put(canonicalAlias, newCommand);
        CommandAlias newAlias = new CommandAlias(CommandAlias.UNREGISTERED_ID, alias);
        aliasMap.put(canonicalAlias, newAlias);
        reverseAliasMap.computeIfAbsent(newCommand, command -> new ArrayList<>()).add(newAlias);

        commandTableEdit.save(newCommand, newAlias, command -> registerCommand(command));
        return commandTableEdit;
    }

    public CommandTableEdit deleteCommand(final String alias) {
        CommandTableEdit commandTableEdit = new CommandTableEdit();
        String canonicalAlias=canonicalize(alias);

        if (aliasMap.containsKey(canonicalAlias)) {
            MessageCommand command = commandMap.get(canonicalAlias);
            CommandAlias commandAlias = aliasMap.get(canonicalAlias);
            List<CommandAlias> oldCommandsAliases = reverseAliasMap.get(command);

            oldCommandsAliases.remove(commandAlias);
            if (oldCommandsAliases.isEmpty()) {
                commandTableEdit.delete(command);
                reverseAliasMap.remove(command);

                idToCommandMap.remove(command.getId());
            }

            commandMap.remove(canonicalAlias);
            commandTableEdit.delete(commandAlias);
        }

        return commandTableEdit;
    }

    public void registerCommand(final HomeCommand homeCommand, final CommandEvent commandEvent) {
        registerCommand(homeCommand);
        eventCommandMap.put(commandEvent, homeCommand);
        eventMap.computeIfAbsent(commandEvent.getEventType(), type -> new ArrayList<>()).add(commandEvent);
        events.add(commandEvent);
    }

    public void registerCommand(final HomeCommand homeCommand, final CommandAlert commandAlert) {
        registerCommand(homeCommand);
        alertCommandMap.put(commandAlert, homeCommand);
        alertMap.computeIfAbsent(commandAlert.getAlertToken(), type -> new ArrayList<>()).add(commandAlert);
        alerts.add(commandAlert);
    }

    public void registerCommand(final MessageCommand command, final String... aliases) {
        for (String alias : aliases) {
            registerCommand(command, new CommandAlias(CommandAlias.UNREGISTERED_ID, alias));
        }
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

    public MessageCommand getCommand(final String token) {
        return commandMap.get(canonicalize(token));
    }

    public List<HomeCommand> getCommands(final CommandEvent.Type eventType) {
        List<CommandEvent> events = eventMap.get(eventType);
        if (events != null) {
            return events.stream().map(event -> getCommand(event)).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    public HomeCommand getCommand(final CommandEvent event) {
        return eventCommandMap.get(event);
    }

    public List<HomeCommand> getCommandsFromToken(final String alertToken) {
        List<CommandAlert> alerts = alertMap.get(alertToken);
        if (alerts != null) {
            return alerts.stream().map(alert -> getCommand(alert)).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    private HomeCommand getCommand(final CommandAlert alert) {
        return alertCommandMap.get(alert);
    }

    private String canonicalize(final String token) {
        return isCaseSensitive ? token : token.toLowerCase();
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

    private void registerCommand(final Command command) {
        idToCommandMap.put(command.getId(), command);
    }
}
