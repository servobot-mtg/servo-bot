package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.model.Home;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public abstract class HomeCommand extends Command {
    static Logger LOGGER = LoggerFactory.getLogger(HomeCommand.class);

    public HomeCommand(final int id) {
        super(id);
    }

    public abstract String getName();
    public abstract void perform(Guild home);
    public abstract void perform(Home home);

    protected static void say(final Guild home, final String channelName, final String message) {
        LOGGER.trace("Trying to say " + message + " to " + channelName);

        List<TextChannel> channels = home.getTextChannelsByName(channelName, false);
        if (channels.size() > 0) {
            channels.get(0).sendMessage(message).queue();
        }
    }
}
