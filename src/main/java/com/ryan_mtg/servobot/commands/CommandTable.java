package com.ryan_mtg.servobot.commands;

import com.google.common.collect.ImmutableMap;
import com.ryan_mtg.servobot.model.alerts.AlertGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CommandTable {
    private static Logger LOGGER = LoggerFactory.getLogger(CommandTable.class);

    private boolean isCaseSensitive;

    private Map<String, MessageCommand> commandMap = new HashMap<>();
    private List<CommandAlias> aliases = new ArrayList<>();

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
        String alias = cannonicalize(commandAlias.getAlias());
        if (commandMap.containsKey(alias)) {
            LOGGER.warn("Command " + alias + " is already registered");
            throw new IllegalStateException("Command " + alias + " is already registered");
        }
        commandMap.put(alias, command);
        aliases.add(commandAlias);
    }

    public void registerCommand(final HomeCommand homeCommand, final CommandEvent commandEvent) {
        eventCommandMap.put(commandEvent, homeCommand);
        eventMap.computeIfAbsent(commandEvent.getEventType(), type -> new ArrayList<>()).add(commandEvent);
        events.add(commandEvent);
    }

    public void registerCommand(final HomeCommand homeCommand, final CommandAlert commandAlert) {
        alertCommandMap.put(commandAlert, homeCommand);
        alertMap.computeIfAbsent(commandAlert.getAlertToken(), type -> new ArrayList<>()).add(commandAlert);
        alerts.add(commandAlert);
    }

    public void registerCommand(final MessageCommand command, final String... aliases) {
        for (String alias : aliases) {
            registerCommand(command, new CommandAlias(CommandAlias.UNREGISTERED_ID, alias));
        }
    }

    public List<CommandAlias> getAliases() {
        return aliases;
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

    public MessageCommand getCommands(final String token) {
        return commandMap.get(cannonicalize(token));
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

    private String cannonicalize(final String token) {
        return isCaseSensitive ? token : token.toLowerCase();
    }

    public void setTimeZone(final String timeZone) {
    }
}
