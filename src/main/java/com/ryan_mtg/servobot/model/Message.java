package com.ryan_mtg.servobot.model;

public interface Message {
    long getId();
    long getChannelId();
    String getContent();
    User getSender();
    boolean canEmote();
    void addEmote(final Emote emote);
}
