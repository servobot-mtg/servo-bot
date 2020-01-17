package com.ryan_mtg.servobot.model.scope;

public class NullSymbolTable implements SymbolTable {
    @Override
    public Object lookup(final String name) {
        return null;
    }
}
