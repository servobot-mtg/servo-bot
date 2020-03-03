package com.ryan_mtg.servobot.model;

import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.user.HomedUser;

import java.util.List;

public interface Home {
    String getName();
    Channel getChannel(String channelName, int serviceType);
    boolean isStreamer(User user);

    String getRole(User user, int serviceType);
    void setRole(User user, String role) throws BotErrorException;
    void setRole(String username, String role) throws BotErrorException;
    List<String> clearRole(String role) throws BotErrorException;
    boolean isHigherRanked(String userName, User sender) throws BotErrorException;

    Emote getEmote(String emoteName);

    User getUser(HomedUser homedUser);

    HomeEditor getHomeEditor();

    void setStatus(String status);
}
