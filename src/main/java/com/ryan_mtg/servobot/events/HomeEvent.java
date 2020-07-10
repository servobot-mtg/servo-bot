package com.ryan_mtg.servobot.events;

import com.ryan_mtg.servobot.model.HomeEditor;
import com.ryan_mtg.servobot.model.scope.SimpleSymbolTable;
import com.ryan_mtg.servobot.model.scope.Scope;

public interface HomeEvent extends BotHomeEvent {
    default Scope getScope() {
        HomeEditor homeEditor = getHomeEditor();
        SimpleSymbolTable symbolTable = new SimpleSymbolTable();
        symbolTable.addValue("home", getServiceHome().getName());

        return new Scope(homeEditor.getScope(), symbolTable);
    }
}
