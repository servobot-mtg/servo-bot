package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.model.Home;
import com.ryan_mtg.servobot.model.User;

public abstract class UserCommand extends Command {
    public UserCommand(final int id, final int flags, final Permission permission) {
        super(id, flags, permission);
    }

    public abstract void perform(Home home, User user) throws BotErrorException;
}
