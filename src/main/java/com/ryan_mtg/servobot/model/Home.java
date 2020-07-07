package com.ryan_mtg.servobot.model;

import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.user.HomedUser;

import java.util.List;

public interface Home {
    ServiceHome getServiceHome(int serviceType);
    Channel getChannel(String channelName, int serviceType);
    String getName();
    String getBotName();
    boolean isStreamer(User user);
    boolean isStreaming();

    String getRole(User user, int serviceType);
    boolean hasRole(User user, String role);
    boolean hasRole(String role);
    void clearRole(User user, String role) throws UserError;
    void setRole(User user, String role) throws UserError;

    List<String> clearRole(String role) throws UserError;
    boolean isHigherRanked(User user, User otherUser) throws UserError;

    boolean hasUser(String userName);
    User getUser(String userName) throws UserError;

    Emote getEmote(String emoteName);
    List<Emote> getEmotes();

    User getUser(HomedUser homedUser);

    HomeEditor getHomeEditor();

    void setStatus(String status);
}
