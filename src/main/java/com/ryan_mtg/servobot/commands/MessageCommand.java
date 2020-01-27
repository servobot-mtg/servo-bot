package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.MessageSentEvent;
import com.ryan_mtg.servobot.model.Channel;

public abstract class MessageCommand extends Command {
    abstract public void perform(MessageSentEvent event, String arguments) throws BotErrorException;

    public MessageCommand(final int id, final int flags, final Permission permission) {
        super(id, flags, permission);
    }

    protected static void say(MessageSentEvent event, final String text) {
        Channel channel = event.getChannel();
        channel.say(text);
    }
}
