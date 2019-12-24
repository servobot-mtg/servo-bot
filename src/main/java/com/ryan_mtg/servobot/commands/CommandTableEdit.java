package com.ryan_mtg.servobot.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class CommandTableEdit {
    private List<Command> deletedCommands = new ArrayList<>();
    private List<Command> savedCommands = new ArrayList<>();
    private List<CommandAlias> deletedAliases = new ArrayList<>();
    private List<CommandEvent> deletedEvents = new ArrayList<>();
    private List<CommandAlert> deletedAlerts = new ArrayList<>();
    private Map<CommandAlias, Integer> savedAliases = new IdentityHashMap<>();
    private Map<Command, CommandAlias> savedCommandToAliasMap = new HashMap<>();
    private Map<Command, Consumer<Command>> commandSaveCallbackMap = new HashMap<>();
    private Map<CommandAlias, BiConsumer<Integer, CommandAlias>> aliasSaveCallbackMap = new IdentityHashMap<>();

    public void delete(final Command command) {
        deletedCommands.add(command);
    }

    public void delete(final CommandAlias commandAlias) {
        deletedAliases.add(commandAlias);
    }

    public void delete(final CommandEvent commandEvent) {
        deletedEvents.add(commandEvent);
    }

    public void delete(final CommandAlert commandAlert) {
        deletedAlerts.add(commandAlert);
    }

    public void save(final Command command, final CommandAlias commandAlias,
                     final Consumer<Command> commandSaveCallback,
                     final BiConsumer<Integer, CommandAlias> aliasSaveCallback) {
        savedCommands.add(command);
        savedCommandToAliasMap.put(command, commandAlias);
        commandSaveCallbackMap.put(command, commandSaveCallback);
        aliasSaveCallbackMap.put(commandAlias, aliasSaveCallback);
    }

    public void save(final int commandId, final CommandAlias commandAlias,
                     final BiConsumer<Integer, CommandAlias> aliasSaveCallback) {
        savedAliases.put(commandAlias, commandId);
        aliasSaveCallbackMap.put(commandAlias, aliasSaveCallback);
    }

    public List<Command> getDeletedCommands() {
        return deletedCommands;
    }

    public List<Command> getSavedCommands() {
        return savedCommands;
    }

    public void commandSaved(final Command command) {
        if (savedCommandToAliasMap.containsKey(command)) {
            savedAliases.put(savedCommandToAliasMap.get(command), command.getId());
        }
        commandSaveCallbackMap.get(command).accept(command);
    }

    public void aliasSaved(final CommandAlias commandAlias) {
        aliasSaveCallbackMap.get(commandAlias).accept(savedAliases.get(commandAlias), commandAlias);
    }

    public Map<CommandAlias, Integer> getSavedAliases() {
        return savedAliases;
    }

    public List<CommandAlias> getDeletedAliases() {
        return deletedAliases;
    }

    public List<CommandEvent> getDeletedEvents() {
        return deletedEvents;
    }

    public List<CommandAlert> getDeletedAlerts() {
        return deletedAlerts;
    }
}
