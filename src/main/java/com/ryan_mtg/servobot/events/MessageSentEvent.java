package com.ryan_mtg.servobot.events;

import com.ryan_mtg.servobot.model.HomeEditor;
import com.ryan_mtg.servobot.model.Message;
import com.ryan_mtg.servobot.model.scope.SimpleSymbolTable;
import com.ryan_mtg.servobot.model.scope.Scope;

public interface MessageSentEvent extends HomeEvent, MessageEvent {
    Message getMessage();

    @Override
    default Scope getScope() {
        HomeEditor homeEditor = getHomeEditor();
        SimpleSymbolTable messageSymbolTable = new SimpleSymbolTable();
        messageSymbolTable.addValue("sender", getSender().getName());
        messageSymbolTable.addValue("home", getServiceHome().getName());

        return new Scope(homeEditor.getScope(), messageSymbolTable);
    }
}
