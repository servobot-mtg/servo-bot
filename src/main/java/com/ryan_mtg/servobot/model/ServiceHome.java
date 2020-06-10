package com.ryan_mtg.servobot.model;

import com.ryan_mtg.servobot.error.UserError;

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
    Channel getChannel(String channelName) throws UserError;

    boolean isStreaming();

    void start(BotHome botHome);
    void stop(BotHome botHome);
    void setHomeEditor(HomeEditor homeEditor);
    void setName(String botName);
}
