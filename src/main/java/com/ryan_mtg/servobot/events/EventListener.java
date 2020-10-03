package com.ryan_mtg.servobot.events;

public interface EventListener {
    void onPrivateMessage(MessageEvent messageEvent);
    void onMessage(MessageHomeEvent messageHomeEvent);

    void onEmoteAdded(EmoteHomeEvent emoteHomeEvent);
    void onEmoteRemoved(EmoteHomeEvent emoteHomeEvent);

    void onStreamStart(StreamStartEvent streamStartEvent);
    void onNewUser(UserHomeEvent newUserEvent);
    void onRaid(UserHomeEvent raidEvent);
    void onSubscribe(UserHomeEvent subscribeEvent);

    void onAlert(AlertEvent alertEvent);
}
