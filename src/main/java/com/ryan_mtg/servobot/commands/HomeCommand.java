package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.model.Home;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class HomeCommand extends Command {
    static Logger LOGGER = LoggerFactory.getLogger(HomeCommand.class);

    public HomeCommand(final int id) {
        super(id);
    }

    public abstract String getName();
    public abstract void perform(Home home);
}
