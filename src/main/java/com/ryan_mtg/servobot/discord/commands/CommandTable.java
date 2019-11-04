package com.ryan_mtg.servobot.discord.commands;

import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CommandTable {
    private static Logger LOGGER = LoggerFactory.getLogger(CommandTable.class);
    private Map<String, MessageCommand> commandMap = new HashMap<>();
    private List<CommandAlias> aliases = new ArrayList<>();
    private List<CommandEvent> events = new ArrayList<>();
    private Map<CommandEvent, HomeCommand> eventCommandMap = new HashMap<>();
    private Map<CommandEvent.Type, List<CommandEvent>> eventMap = new HashMap<>();

    private boolean isCaseSensitive;

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

    public Map<String, MessageCommand> getCommandList() {
        return ImmutableMap.copyOf(commandMap);
    }

    public MessageCommand getCommand(final String token) {
        return commandMap.get(cannonicalize(token));
    }

    public List<HomeCommand> getCommand(final CommandEvent.Type eventType) {
        List<CommandEvent> events = eventMap.get(eventType);
        if (events != null) {
            return events.stream().map(event -> getCommand(event)).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    public HomeCommand getCommand(final CommandEvent event) {
        return eventCommandMap.get(event);
    }

    private String cannonicalize(final String token) {
        return isCaseSensitive ? token : token.toLowerCase();
    }
}
