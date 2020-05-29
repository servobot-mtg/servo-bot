package com.ryan_mtg.servobot.events;

public interface BotThrowingFunction <ParameterType, ReturnType> {
    ReturnType apply(ParameterType parameter) throws BotErrorException;
}
