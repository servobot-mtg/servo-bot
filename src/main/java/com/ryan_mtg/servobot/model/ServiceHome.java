package com.ryan_mtg.servobot.model;

import com.ryan_mtg.servobot.user.HomedUser;

import java.util.List;

public interface ServiceHome {
    int getServiceType();
    String getDescription();
    String getLink();
    Service getService();
    Home getHome();
    List<String> getEmotes();

    void start(BotHome botHome);
    void stop(BotHome botHome);
    void setHomeEditor(HomeEditor homeEditor);
}
