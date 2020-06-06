package com.ryan_mtg.servobot.model;

import com.ryan_mtg.servobot.user.HomedUser;

public interface User {
    String getName();
    int getId();
    HomedUser getHomedUser();
    com.ryan_mtg.servobot.user.User getUser();
    boolean isBot();
    boolean isAdmin();
    boolean isModerator();
    boolean isSubscriber();
}
