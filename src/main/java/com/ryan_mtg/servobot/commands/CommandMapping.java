package com.ryan_mtg.servobot.commands;

import java.util.Map;

public class CommandMapping {
    private Map<Integer, Command> idToCommandMap;
    private Map<Trigger, Command> triggerCommandMap;

    public CommandMapping(final Map<Integer, Command> idToCommandMap, final Map<Trigger, Command> triggerCommandMap) {
        this.idToCommandMap = idToCommandMap;
        this.triggerCommandMap = triggerCommandMap;
    }

    public Map<Integer, Command> getIdToCommandMap() {
        return idToCommandMap;
    }

    public Map<Trigger, Command> getTriggerCommandMap() {
        return triggerCommandMap;
    }
}
