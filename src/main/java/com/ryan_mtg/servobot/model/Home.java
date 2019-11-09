package com.ryan_mtg.servobot.model;

public interface Home {
    String getName();
    Channel getChannel(String channelName);
    boolean isStreamer(User user);
    String getRole(User user);
    boolean hasEmotes();
    Emote getEmote(String emoteName);
}
