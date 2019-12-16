package com.ryan_mtg.servobot.commands;

import java.util.Map;

public class CommandMapping {
    private Map<Integer, Command> idtoCommandMap;
    private Map<CommandAlias, MessageCommand> aliasCommandMap;
    private Map<CommandEvent, Command> eventCommandMap;
    private Map<CommandAlert, HomeCommand> alertCommandMap;

    public CommandMapping(final Map<Integer, Command> idToCommandMap,
                          final Map<CommandAlias, MessageCommand> aliasCommandMap,
                          final Map<CommandEvent, Command> eventCommandMap,
                          final Map<CommandAlert, HomeCommand> alertCommandMap) {
        this.idtoCommandMap = idToCommandMap;
        this.aliasCommandMap = aliasCommandMap;
        this.eventCommandMap = eventCommandMap;
        this.alertCommandMap = alertCommandMap;
    }

    public Map<Integer, Command> getIdtoCommandMap() {
        return idtoCommandMap;
    }

    public Map<CommandAlias, MessageCommand> getAliasCommandMap() {
        return aliasCommandMap;
    }

    public Map<CommandEvent, Command> getEventCommandMap() {
        return eventCommandMap;
    }

    public Map<CommandAlert, HomeCommand> getAlertCommandMap() {
        return alertCommandMap;
    }
}
