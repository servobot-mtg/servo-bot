package com.ryan_mtg.servobot.model;

import com.ryan_mtg.servobot.events.BotErrorException;

public interface Home {
    String getName();
    Channel getChannel(String channelName, int serviceType);
    boolean isStreamer(User user);

    String getRole(User user, int serviceType);
    void setRole(User user, String role) throws BotErrorException;

    Emote getEmote(String emoteName);

    HomeEditor getHomeEditor();
}
