package com.ryan_mtg.servobot.events;

public interface EventListener {
    void onMessage(MessageSentEvent messageSentEvent) throws BotErrorException;

    void onStreamStart(StreamStartEvent streamStartEvent);
    void onNewUser(NewUserEvent newUserEvent) throws BotErrorException;

    void onAlert(AlertEvent alertEvent);
}
