package com.ryan_mtg.servobot.model;

public interface ServiceHome {
    int getServiceType();
    String getDescription();
    String getLink();
    Service getService();
    Home getHome();

    void start(BotHome botHome);
    void stop(BotHome botHome);
    void setHomeEditor(HomeEditor homeEditor);
}
