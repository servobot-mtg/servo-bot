package com.ryan_mtg.servobot.events;

public interface EventListener {
    void onMessage(MessageHomeEvent messageHomeEvent) throws BotErrorException;

    void onStreamStart(StreamStartEvent streamStartEvent);
    void onNewUser(UserHomeEvent newUserEvent) throws BotErrorException;
    void onRaid(UserHomeEvent raidEvent) throws BotErrorException;
    void onSubscribe(UserHomeEvent subscribeEvent) throws BotErrorException;

    void onAlert(AlertEvent alertEvent);
}
