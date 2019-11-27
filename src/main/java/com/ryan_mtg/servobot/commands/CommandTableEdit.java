package com.ryan_mtg.servobot.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class CommandTableEdit {
    private List<MessageCommand> deletedCommands = new ArrayList<>();
    private List<MessageCommand> savedCommands = new ArrayList<>();
    private List<CommandAlias> deletedAliases = new ArrayList<>();
    private Map<MessageCommand, CommandAlias> savedAliases = new HashMap<>();
    private Map<MessageCommand, Consumer<MessageCommand>> callbackMap = new HashMap<>();

    public void delete(final MessageCommand command) {
        deletedCommands.add(command);
    }

    public void delete(final CommandAlias commandAlias) {
        deletedAliases.add(commandAlias);
    }

    public void save(final MessageCommand command, final CommandAlias commandAlias,
                     final Consumer<MessageCommand> saveCallback) {
        savedCommands.add(command);
        savedAliases.put(command, commandAlias);
        callbackMap.put(command, saveCallback);
    }

    public List<MessageCommand> getDeletedCommands() {
        return deletedCommands;
    }

    public List<MessageCommand> getSavedCommands() {
        return savedCommands;
    }

    public void commandSaved(final MessageCommand messageCommand) {
        callbackMap.get(messageCommand).accept(messageCommand);
    }

    public CommandAlias getSavedAlias(final MessageCommand messageCommand) {
        return savedAliases.get(messageCommand);

    }

    public List<CommandAlias> getDeletedAliases() {
        return deletedAliases;
    }
}
