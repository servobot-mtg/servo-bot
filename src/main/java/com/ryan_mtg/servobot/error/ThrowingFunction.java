package com.ryan_mtg.servobot.error;

public interface ThrowingFunction <ReturnType> {
    ReturnType apply() throws Exception;
}
