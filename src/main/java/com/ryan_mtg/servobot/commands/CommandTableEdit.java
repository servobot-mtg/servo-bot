package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.commands.hierarchy.Command;
import com.ryan_mtg.servobot.commands.trigger.Trigger;
import com.ryan_mtg.servobot.model.alerts.AlertGenerator;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class CommandTableEdit {
    @Getter
    private List<Command> deletedCommands = new ArrayList<>();

    @Getter
    private Map<Command, Integer> savedCommands = new HashMap<>();

    @Getter
    private List<Trigger> deletedTriggers = new ArrayList<>();

    @Getter
    private Map<Trigger, Integer> savedTriggers = new IdentityHashMap<>();
    private Map<Command, List<Trigger>> savedCommandToTriggersMap = new HashMap<>();
    private Map<Trigger, Command> savedTriggerToCommandMap = new IdentityHashMap<>();
    private Map<Command, Consumer<Command>> commandSaveCallbackMap = new HashMap<>();
    private Map<Trigger, BiConsumer<Command, Trigger>> triggerSaveCallbackMap = new IdentityHashMap<>();

    @Getter
    private Map<AlertGenerator, Integer> savedAlertGenerators = new HashMap<>();

    @Getter
    private List<AlertGenerator> deletedAlertGenerators = new ArrayList<>();

    public void delete(final Command command) {
        deletedCommands.add(command);
    }

    public void save(final int contextId, final Command command) {
        savedCommands.put(command, contextId);
    }

    public void save(final int contextId, final Command command, final Consumer<Command> commandSaveCallback) {
        save(contextId, command);
        commandSaveCallbackMap.put(command, commandSaveCallback);
    }

    public void save(final int contextId, final Command command, final Trigger trigger,
            final BiConsumer<Command, Trigger> triggerSaveCallback) {
        save(contextId, command);
        savedCommandToTriggersMap.computeIfAbsent(command, c -> new ArrayList<>()).add(trigger);

        save(command.getId(), trigger);
        savedTriggerToCommandMap.put(trigger, command);
        triggerSaveCallbackMap.put(trigger, triggerSaveCallback);
    }

    public void save(final int commandId, final Trigger trigger) {
        savedTriggers.put(trigger, commandId);
    }

    public void commandSaved(final Command command) {
        if (savedCommandToTriggersMap.containsKey(command)) {
            for (Trigger trigger : savedCommandToTriggersMap.get(command)) {
                savedTriggers.put(trigger, command.getId());
            }
        }
        if (commandSaveCallbackMap.containsKey(command)) {
            commandSaveCallbackMap.get(command).accept(command);
        }
    }

    public void triggerSaved(final Trigger trigger) {
        if (triggerSaveCallbackMap.containsKey(trigger)) {
            triggerSaveCallbackMap.get(trigger).accept(savedTriggerToCommandMap.get(trigger), trigger);
        }
    }

    public void delete(final Trigger trigger) {
        deletedTriggers.add(trigger);
    }

    public void save(final int botHomeId, final AlertGenerator alertGenerator) {
        savedAlertGenerators.put(alertGenerator, botHomeId);
    }

    public void delete(final AlertGenerator alertGenerator) {
        deletedAlertGenerators.add(alertGenerator);
    }

    public void merge(final CommandTableEdit commandTableEdit) {
        deletedCommands.addAll(commandTableEdit.deletedCommands);
        savedCommands.putAll(commandTableEdit.savedCommands);

        deletedTriggers.addAll(commandTableEdit.deletedTriggers);

        savedTriggers.putAll(commandTableEdit.savedTriggers);
        savedCommandToTriggersMap.putAll(commandTableEdit.savedCommandToTriggersMap);
        savedTriggerToCommandMap.putAll(commandTableEdit.savedTriggerToCommandMap);
        commandSaveCallbackMap.putAll(commandTableEdit.commandSaveCallbackMap);
        triggerSaveCallbackMap.putAll(commandTableEdit.triggerSaveCallbackMap);

        savedAlertGenerators.putAll(commandTableEdit.savedAlertGenerators);
        deletedAlertGenerators.addAll(deletedAlertGenerators);
    }
}
