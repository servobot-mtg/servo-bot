package com.ryan_mtg.servobot.events;

import com.ryan_mtg.servobot.model.Channel;
import com.ryan_mtg.servobot.model.HomeEditor;
import com.ryan_mtg.servobot.model.Message;
import com.ryan_mtg.servobot.model.User;
import com.ryan_mtg.servobot.model.scope.FunctorSymbolTable;
import com.ryan_mtg.servobot.model.scope.Scope;

public interface MessageSentEvent extends HomeEvent {
    Channel getChannel();
    Message getMessage();
    User getSender();

    @Override
    default Scope getScope() {
        HomeEditor homeEditor = getHomeEditor();
        FunctorSymbolTable messageSymbolTable = new FunctorSymbolTable();
        messageSymbolTable.addValue("sender", getSender().getName());
        messageSymbolTable.addValue("home", getHome().getName());

        return new Scope(homeEditor.getScope(), messageSymbolTable);
    }

}
