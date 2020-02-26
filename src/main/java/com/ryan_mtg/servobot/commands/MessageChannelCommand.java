package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.model.Home;
import com.ryan_mtg.servobot.utility.Validation;

public class MessageChannelCommand extends HomeCommand {
    public static final int TYPE = 4;
    private final int serviceType;
    private final String channelName;
    private final String message;

    public MessageChannelCommand(final int id, final CommandSettings commandSettings, final int serviceType,
                                 final String channelName, final String message) throws BotErrorException {
        super(id, commandSettings);
        this.serviceType = serviceType;
        this.channelName = channelName;
        this.message = message;

        Validation.validateStringLength(channelName, Validation.MAX_CHANNEL_NAME_LENGTH, "Channel name");
        Validation.validateStringLength(message, Validation.MAX_TEXT_LENGTH, "Message");
    }

    @Override
    public int getType() {
        return TYPE;
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
