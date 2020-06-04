package com.ryan_mtg.servobot.events;

import com.ryan_mtg.servobot.model.BotEditor;
import com.ryan_mtg.servobot.model.Channel;
import com.ryan_mtg.servobot.model.Home;
import com.ryan_mtg.servobot.model.HomeEditor;
import com.ryan_mtg.servobot.model.Message;
import com.ryan_mtg.servobot.model.ServiceHome;
import com.ryan_mtg.servobot.model.User;
import com.ryan_mtg.servobot.model.editors.BookTableEditor;
import com.ryan_mtg.servobot.model.editors.CommandTableEditor;

public class MessageInvokedHomeEvent implements CommandInvokedHomeEvent {
    private MessageHomeEvent messageHomeEvent;
    private String command;
    private String arguments;

    public MessageInvokedHomeEvent(final MessageHomeEvent messageHomeEvent, final String command,
            final String arguments) {
        this.messageHomeEvent = messageHomeEvent;
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
        return messageHomeEvent.getHomeEditor().getCommandTableEditor();
    }

    @Override
    public BookTableEditor getBookTableEditor() {
        return messageHomeEvent.getHomeEditor().getBookTableEditor();
    }

    @Override
    public Home getHome() {
        return messageHomeEvent.getHome();
    }

    @Override
    public int getHomeId() {
        return messageHomeEvent.getHomeId();
    }

    @Override
    public ServiceHome getServiceHome(final int serviceType) {
        return messageHomeEvent.getServiceHome(serviceType);
    }

    @Override
    public HomeEditor getHomeEditor() {
        return messageHomeEvent.getHomeEditor();
    }

    @Override
    public void setHomeEditor(final HomeEditor homeEditor) {
        throw new UnsupportedOperationException("This operation is not supported");
    }

    @Override
    public Channel getChannel() {
        return messageHomeEvent.getChannel();
    }

    @Override
    public User getSender() {
        return messageHomeEvent.getSender();
    }

    @Override
    public Message getMessage() {
        return messageHomeEvent.getMessage();
    }

    @Override
    public BotEditor getBotEditor() {
        return messageHomeEvent.getBotEditor();
    }

    @Override
    public void setBotEditor(final BotEditor botEditor) {
        throw new UnsupportedOperationException("This operation is not supported");
    }

    @Override
    public int getServiceType() {
        return messageHomeEvent.getServiceType();
    }
}
