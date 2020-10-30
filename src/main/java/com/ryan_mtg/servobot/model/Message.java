package com.ryan_mtg.servobot.model;

public interface Message {
    long getId();
    long getChannelId();
    String getContent();
    User getSender();
    boolean canEmote();
    void addEmote(Emote emote);
    void removeEmote(Emote emote, User user);

    void updateText(String text);
    boolean isOld();
}
