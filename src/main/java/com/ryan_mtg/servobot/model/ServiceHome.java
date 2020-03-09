package com.ryan_mtg.servobot.model;

import java.util.List;

public interface ServiceHome {
    int getServiceType();
    String getDescription();

    String getLink();
    String getImageUrl();

    Service getService();
    Home getHome();
    List<String> getEmotes();
    List<String> getRoles();
    List<String> getChannels();

    void start(BotHome botHome);
    void stop(BotHome botHome);
    void setHomeEditor(HomeEditor homeEditor);
    void setName(String botName);
}
