package com.ryan_mtg.servobot.model;

public interface ServiceHome {
    int getServiceType();
    String getDescription();
    Service getService();
    Home getHome();

    void start(BotHome botHome);
}
