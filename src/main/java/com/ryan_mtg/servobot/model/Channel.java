package com.ryan_mtg.servobot.model;

public interface Channel {
    Home getHome();
    void say(String message);
}
