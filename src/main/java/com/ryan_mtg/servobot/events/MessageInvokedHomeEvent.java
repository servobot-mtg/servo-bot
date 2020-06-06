package com.ryan_mtg.servobot.events;

import com.ryan_mtg.servobot.model.Home;
import com.ryan_mtg.servobot.model.HomeEditor;
import com.ryan_mtg.servobot.model.ServiceHome;
import com.ryan_mtg.servobot.model.editors.BookTableEditor;
import com.ryan_mtg.servobot.model.editors.CommandTableEditor;

public class MessageInvokedHomeEvent extends MessageInvokedEvent implements CommandInvokedHomeEvent {
    private MessageHomeEvent messageHomeEvent;

    public MessageInvokedHomeEvent(final MessageHomeEvent messageHomeEvent, final String command,
            final String arguments) {
        super(messageHomeEvent, command, arguments);
        this.messageHomeEvent = messageHomeEvent;
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
}
