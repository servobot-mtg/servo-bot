package com.ryan_mtg.servobot.commands.chat;

import com.ryan_mtg.servobot.commands.CommandType;
import com.ryan_mtg.servobot.commands.CommandVisitor;
import com.ryan_mtg.servobot.commands.Permission;
import com.ryan_mtg.servobot.commands.hierarchy.Command;
import com.ryan_mtg.servobot.commands.hierarchy.HomeCommand;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.events.HomeEvent;
import com.ryan_mtg.servobot.model.Channel;
import com.ryan_mtg.servobot.model.scope.SimpleSymbolTable;
import com.ryan_mtg.servobot.model.scope.Scope;
import com.ryan_mtg.servobot.utility.Validation;
import lombok.Getter;

public class MessageChannelCommand extends HomeCommand {
    public static final CommandType TYPE = CommandType.MESSAGE_CHANNEL_COMMAND_TYPE;

    @Getter
    private final int serviceType;

    @Getter
    private final String channelName;

    @Getter
    private final String message;

    public MessageChannelCommand(final int id, final int flags, final Permission permission,
            final int serviceType, final String channelName, final String message) throws BotErrorException {
        super(id, flags, permission);
        this.serviceType = serviceType;
        this.channelName = channelName;
        this.message = message;

        Validation.validateStringLength(channelName, Validation.MAX_CHANNEL_NAME_LENGTH, "Channel name");
        Validation.validateStringLength(message, Validation.MAX_TEXT_LENGTH, "Message");
    }

    @Override
    public CommandType getType() {
        return TYPE;
    }

    @Override
    public void perform(final HomeEvent homeEvent) throws BotErrorException {
        Channel channel = homeEvent.getHome().getChannel(channelName, serviceType);
        SimpleSymbolTable symbolTable = new SimpleSymbolTable();
        symbolTable.addValue("commandCount", "");
        Scope commandScope = new Scope(homeEvent.getScope(), symbolTable);
        Command.say(channel, homeEvent, commandScope, message);
    }

    @Override
    public void acceptVisitor(final CommandVisitor commandVisitor) {
        commandVisitor.visitMessageChannelCommand(this);
    }
}
