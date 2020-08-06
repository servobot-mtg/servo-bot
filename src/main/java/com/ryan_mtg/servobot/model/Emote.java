package com.ryan_mtg.servobot.model;

public interface Emote {
    String getName();
    String getMessageText();
    String getImageUrl();
    boolean isPermitted();
}
