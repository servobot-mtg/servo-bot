package com.ryan_mtg.servobot.commands.hierarchy;

import com.ryan_mtg.servobot.commands.CommandSettings;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.model.Home;
import com.ryan_mtg.servobot.model.User;

public abstract class UserCommand extends Command {
    public UserCommand(final int id, final CommandSettings commandSettings) {
        super(id, commandSettings);
    }

    public abstract void perform(Home home, User user) throws BotErrorException;
}
