package com.ryan_mtg.servobot.commands.hierarchy;

import com.ryan_mtg.servobot.commands.Permission;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.HomeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class HomeCommand extends Command {
    static Logger LOGGER = LoggerFactory.getLogger(HomeCommand.class);

    public HomeCommand(final int id, final int flags, final Permission permission) {
        super(id, flags, permission);
    }

    public abstract void perform(HomeEvent homeEvent) throws BotErrorException;
}
