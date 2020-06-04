package com.ryan_mtg.servobot.model;

public interface Message {
    String getContent();
    Channel getChannel();
    User getSender();
    boolean canEmote();
    void addEmote(final Emote emote);
}
