package com.ryan_mtg.servobot.events;

import com.ryan_mtg.servobot.model.BotEditor;
import com.ryan_mtg.servobot.model.Channel;
import com.ryan_mtg.servobot.model.Message;
import com.ryan_mtg.servobot.model.User;
import com.ryan_mtg.servobot.model.editors.BookTableEditor;
import com.ryan_mtg.servobot.model.editors.CommandTableEditor;
import com.ryan_mtg.servobot.model.editors.StorageValueEditor;
import com.ryan_mtg.servobot.model.scope.Scope;

public class MessageInvokedEvent implements CommandInvokedEvent {
    private final MessageEvent messageEvent;
    private final String command;
    private final String arguments;

    public MessageInvokedEvent(final MessageEvent messageEvent, final String command, final String arguments) {
        this.messageEvent = messageEvent;
        this.command = command;
        this.arguments = arguments;
    }

    @Override
    public String getCommand() {
        return command;
    }

    @Override
    public String getArguments() {
        return arguments;
    }

    @Override
    public CommandTableEditor getCommandTableEditor() {
        return messageEvent.getBotEditor().getCommandTableEditor();
    }

    @Override
    public BookTableEditor getBookTableEditor() {
        return messageEvent.getBotEditor().getBookTableEditor();
    }

    @Override
    public StorageValueEditor getStorageValueEditor() {
        return messageEvent.getStorageValueEditor();
    }

    @Override
    public Channel getChannel() {
        return messageEvent.getChannel();
    }

    @Override
    public User getSender() {
        return messageEvent.getSender();
    }

    @Override
    public Message getMessage() {
        return messageEvent.getMessage();
    }

    @Override
    public BotEditor getBotEditor() {
        return messageEvent.getBotEditor();
    }

    @Override
    public void setBotEditor(final BotEditor botEditor) {
        throw new UnsupportedOperationException("This operation is not supported");
    }

    @Override
    public int getServiceType() {
        return messageEvent.getServiceType();
    }

    @Override
    public Scope getScope() {
        return messageEvent.getScope();
    }
}
