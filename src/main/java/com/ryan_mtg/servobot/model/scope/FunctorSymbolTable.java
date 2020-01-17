package com.ryan_mtg.servobot.model.scope;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class FunctorSymbolTable implements SymbolTable {
    private Map<String, Supplier<Object>> functorMap = new HashMap<>();

    public void addFunctor(final String name, final Supplier<Object> functor) {
        functorMap.put(name, functor);
    }

    @Override
    public Object lookup(final String name) {
        Supplier<Object> functor = functorMap.get(name);
        if (functor == null) {
            return null;
        }

        return functor.get();
    }
}
