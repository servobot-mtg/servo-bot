package com.ryan_mtg.servobot.discord.commands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public abstract class MessageCommand extends Command {
    abstract public String getName();
    abstract public void perform(Message message, String arguments);

    public MessageCommand(final int id) {
        super(id);
    }

    protected static void say(final Message message, final String text) {
        MessageChannel channel = message.getChannel();
        channel.sendMessage(text).queue();
    }
}
