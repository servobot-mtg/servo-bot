package com.ryan_mtg.servobot.model;

import java.util.List;

public interface ServiceHome {
    Service getService();
    Home getHome();

    int getServiceType();
    String getDescription();

    String getLink();
    String getImageUrl();
    List<String> getEmotes();
    List<String> getRoles();
    List<String> getChannels();

    boolean isStreaming();

    void start(BotHome botHome);
    void stop(BotHome botHome);
    void setHomeEditor(HomeEditor homeEditor);
    void setName(String botName);
}
