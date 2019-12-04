package com.ryan_mtg.servobot.model;

import com.ryan_mtg.servobot.user.HomedUser;

public interface User {
    String getName();
    HomedUser getHomedUser();
    boolean isBot();
    boolean isAdmin();
    boolean isModerator();
    boolean isSubscriber();
    void whisper(String message);
}
