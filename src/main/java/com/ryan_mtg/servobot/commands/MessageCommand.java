package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.model.Channel;
import com.ryan_mtg.servobot.model.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public abstract class MessageCommand extends Command {
    abstract public String getName();
    abstract public void perform(Message message, String arguments);
    abstract public void perform(net.dv8tion.jda.api.entities.Message message, String arguments);

    public MessageCommand(final int id) {
        super(id);
    }

    protected static void say(net.dv8tion.jda.api.entities.Message message, final String text) {
        MessageChannel channel = message.getChannel();
        channel.sendMessage(text).queue();
    }

    protected static void say(Message message, final String text) {
        Channel channel = message.getChannel();
        channel.say(text);
    }
}
