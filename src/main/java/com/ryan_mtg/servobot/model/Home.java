package com.ryan_mtg.servobot.model;

public interface Home {
    String getName();
    Channel getChannel(String channelName, int serviceType);
    boolean isStreamer(User user);
    String getRole(User user, int serviceType);
    Emote getEmote(String emoteName);

    HomeEditor getHomeEditor();
}
