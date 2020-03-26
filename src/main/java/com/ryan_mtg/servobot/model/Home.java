package com.ryan_mtg.servobot.model;

import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.user.HomedUser;

import java.util.List;

public interface Home {
    String getName();
    Channel getChannel(String channelName, int serviceType);
    boolean isStreamer(User user);
    boolean isStreaming();

    String getRole(User user, int serviceType);
    boolean hasRole(User user, String role);
    void clearRole(User user, String role) throws BotErrorException;
    void setRole(User user, String role) throws BotErrorException;

    List<String> clearRole(String role) throws BotErrorException;
    boolean isHigherRanked(User user, User otherUser) throws BotErrorException;

    User getUser(String userName) throws BotErrorException;

    Emote getEmote(String emoteName);

    User getUser(HomedUser homedUser);

    HomeEditor getHomeEditor();

    void setStatus(String status);
}
