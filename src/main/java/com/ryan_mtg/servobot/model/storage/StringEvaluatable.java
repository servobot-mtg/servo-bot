package com.ryan_mtg.servobot.model.storage;

public class StringEvaluatable implements Evaluatable {
    private final String value;

    public StringEvaluatable(final String value) {
        this.value = value;
    }

    @Override
    public String evaluate() {
        return value;
    }
}
