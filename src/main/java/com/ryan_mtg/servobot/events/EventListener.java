package com.ryan_mtg.servobot.events;

public interface EventListener {
    void onMessage(MessageSentEvent messageSentEvent) throws BotErrorException;

    void onStreamStart(StreamStartEvent streamStartEvent);
    void onNewUser(UserEvent newUserEvent) throws BotErrorException;
    void onRaid(UserEvent raidEvent) throws BotErrorException;
    void onSubscribe(UserEvent subscribeEvent) throws BotErrorException;

    void onAlert(AlertEvent alertEvent);
}
