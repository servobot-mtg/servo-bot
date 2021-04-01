package com.ryan_mtg.servobot.model.scope;

public class Scope {
    private final Scope parentScope;
    private final SymbolTable symbolTable;

    public Scope(final Scope parentScope, final SymbolTable symbolTable) {
        this.parentScope = parentScope;
        this.symbolTable = symbolTable;
    }

    public Object lookup(final String name) {
        Object value = symbolTable.lookup(name);
        if (value != null) {
            return value;
        }

        if (parentScope != null) {
            return parentScope.lookup(name);
        }

        return null;
    }
}
