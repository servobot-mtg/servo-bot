package com.ryan_mtg.servobot.events;

import com.ryan_mtg.servobot.model.HomeEditor;
import com.ryan_mtg.servobot.model.ServiceHome;
import com.ryan_mtg.servobot.model.editors.BookTableEditor;
import com.ryan_mtg.servobot.model.editors.CommandTableEditor;
import com.ryan_mtg.servobot.model.scope.Scope;
import com.ryan_mtg.servobot.model.scope.SimpleSymbolTable;

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
    public ServiceHome getServiceHome() {
        return messageHomeEvent.getServiceHome();
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
    public Scope getScope() {
        HomeEditor homeEditor = getHomeEditor();
        SimpleSymbolTable messageSymbolTable = new SimpleSymbolTable();
        messageSymbolTable.addValue("sender", getSender().getName());
        messageSymbolTable.addValue("home", getServiceHome().getName());

        return new Scope(homeEditor.getScope(), messageSymbolTable);
    }
}
