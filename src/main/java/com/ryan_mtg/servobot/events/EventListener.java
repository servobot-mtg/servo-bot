package com.ryan_mtg.servobot.events;

public interface EventListener {
    void onMessage(MessageSentEvent messageSentEvent) ;

    void onStreamStart(StreamStartEvent streamStartEvent);

    void onAlert(AlertEvent alertEvent);
}
