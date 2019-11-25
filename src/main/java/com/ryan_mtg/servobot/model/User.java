package com.ryan_mtg.servobot.model;

public interface User {
    String getName();
    boolean isBot();
    boolean isAdmin();
    boolean isModerator();
    boolean isSubscriber();
}
