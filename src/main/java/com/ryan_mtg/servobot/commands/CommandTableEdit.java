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

    private List<Trigger> deletedTriggers = new ArrayList<>();

    private Map<Trigger, Integer> savedTriggers = new IdentityHashMap<>();
    private Map<Command, Trigger> savedCommandToTriggerMap = new HashMap<>();
    private Map<Command, Consumer<Command>> commandSaveCallbackMap = new HashMap<>();
    private Map<Trigger, BiConsumer<Integer, Trigger>> triggerSaveCallbackMap = new IdentityHashMap<>();

    public void delete(final Command command) {
        deletedCommands.add(command);
    }

    public void save(final Command command, final Trigger trigger,
                     final Consumer<Command> commandSaveCallback,
                     final BiConsumer<Integer, Trigger> aliasSaveCallback) {
        savedCommands.add(command);
        savedCommandToTriggerMap.put(command, trigger);
        commandSaveCallbackMap.put(command, commandSaveCallback);
        triggerSaveCallbackMap.put(trigger, aliasSaveCallback);
    }

    public void save(final int commandId, final Trigger trigger,
                     final BiConsumer<Integer, Trigger> triggerSaveCallback) {
        savedTriggers.put(trigger, commandId);
        triggerSaveCallbackMap.put(trigger, triggerSaveCallback);
    }

    public List<Command> getDeletedCommands() {
        return deletedCommands;
    }

    public List<Command> getSavedCommands() {
        return savedCommands;
    }

    public void commandSaved(final Command command) {
        if (savedCommandToTriggerMap.containsKey(command)) {
            savedTriggers.put(savedCommandToTriggerMap.get(command), command.getId());
        }
        commandSaveCallbackMap.get(command).accept(command);
    }

    public void triggerSaved(final Trigger trigger) {
        triggerSaveCallbackMap.get(trigger).accept(savedTriggers.get(trigger), trigger);
    }

    public Map<Trigger, Integer> getSavedTriggers() {
        return savedTriggers;
    }

    public List<Trigger> getDeletedTriggers() {
        return deletedTriggers;
    }

    public void delete(final Trigger trigger) {
        deletedTriggers.add(trigger);
    }
}
