package com.ryan_mtg.servobot.events;

import com.ryan_mtg.servobot.model.Home;
import com.ryan_mtg.servobot.model.HomeEditor;
import com.ryan_mtg.servobot.model.scope.FunctorSymbolTable;
import com.ryan_mtg.servobot.model.scope.Scope;

public interface HomeEvent extends Event {
    Home getHome();
    int getServiceType();

    default Scope getScope() {
        HomeEditor homeEditor = getHomeEditor();
        FunctorSymbolTable symbolTable = new FunctorSymbolTable();
        symbolTable.addValue("home", getHome().getName());

        return new Scope(homeEditor.getScope(), symbolTable);
    }
}
