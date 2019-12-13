package com.ryan_mtg.servobot.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class CommandTableEdit {
    private List<Command> deletedCommands = new ArrayList<>();
    private List<Command> savedCommands = new ArrayList<>();
    private List<CommandAlias> deletedAliases = new ArrayList<>();
    private List<CommandEvent> deletedEvents = new ArrayList<>();
    private List<CommandAlert> deletedAlerts = new ArrayList<>();
    private Map<Command, CommandAlias> savedAliases = new HashMap<>();
    private Map<Command, Consumer<Command>> callbackMap = new HashMap<>();

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

    public void save(final Command command, final CommandAlias commandAlias, final Consumer<Command> saveCallback) {
        savedCommands.add(command);
        savedAliases.put(command, commandAlias);
        callbackMap.put(command, saveCallback);
    }

    public List<Command> getDeletedCommands() {
        return deletedCommands;
    }

    public List<Command> getSavedCommands() {
        return savedCommands;
    }

    public void commandSaved(final Command command) {
        callbackMap.get(command).accept(command);
    }

    public CommandAlias getSavedAlias(final Command messageCommand) {
        return savedAliases.get(messageCommand);
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
