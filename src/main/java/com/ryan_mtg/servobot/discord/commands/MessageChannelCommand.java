package com.ryan_mtg.servobot.discord.commands;

import net.dv8tion.jda.api.entities.Guild;

public class MessageChannelCommand extends HomeCommand {
    public static final int TYPE = 4;
    private final String channelName;
    private final String message;

    public MessageChannelCommand(final int id, final String channelName, final String message) {
        super(id);
        this.channelName = channelName;
        this.message = message;
    }

    @Override
    public int getType() {
        return TYPE;
    }

    @Override
    public String getName() {
        return "MessageChannelCommand";
    }

    @Override
    public void perform(final Guild home) {
        HomeCommand.say(home, channelName, message);
    }

    @Override
    public void acceptVisitor(final CommandVisitor commandVisitor) {
        commandVisitor.visitMessageChannelCommmand(this);
    }

    public String getChannelName() {
        return channelName;
    }

    public String getMessage() {
        return message;
    }
}
