package com.ryan_mtg.servobot.events;

public interface EventListener {
    void onMessage(MessageSentEvent messageSentEvent) throws BotErrorException;

    void onStreamStart(StreamStartEvent streamStartEvent);

    void onAlert(AlertEvent alertEvent);
}
