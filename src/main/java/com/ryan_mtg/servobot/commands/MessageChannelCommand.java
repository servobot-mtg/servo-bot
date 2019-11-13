package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.model.Home;

public class MessageChannelCommand extends HomeCommand {
    public static final int TYPE = 4;
    private final int serviceType;
    private final String channelName;
    private final String message;

    public MessageChannelCommand(final int id, final int serviceType, final String channelName, final String message) {
        super(id);
        this.serviceType = serviceType;
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
    public void perform(final Home home) {
        home.getChannel(channelName, serviceType).say(message);
    }

    @Override
    public void acceptVisitor(final CommandVisitor commandVisitor) {
        commandVisitor.visitMessageChannelCommand(this);
    }

    public int getServiceType() {
        return serviceType;
    }

    public String getChannelName() {
        return channelName;
    }

    public String getMessage() {
        return message;
    }
}
