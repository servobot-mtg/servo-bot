package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.commands.hierarchy.Command;
import com.ryan_mtg.servobot.commands.trigger.Trigger;
import lombok.Getter;

import java.util.Map;

public class CommandMapping {
    @Getter
    private Map<Integer, Command> idToCommandMap;

    @Getter
    private Map<Trigger, Command> triggerCommandMap;

    public CommandMapping(final Map<Integer, Command> idToCommandMap, final Map<Trigger, Command> triggerCommandMap) {
        this.idToCommandMap = idToCommandMap;
        this.triggerCommandMap = triggerCommandMap;
    }
}
