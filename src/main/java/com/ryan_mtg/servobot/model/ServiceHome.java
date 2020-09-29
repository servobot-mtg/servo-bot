package com.ryan_mtg.servobot.model;

import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.user.HomedUser;

import java.util.List;
import java.util.Map;

public interface ServiceHome {
    Service getService();
    int getServiceType();

    String getName();
    String getBotName();
    String getLink();
    String getImageUrl();
    String getDescription();

    boolean isStreaming();
    boolean isStreamer(User user);

    HomeEditor getHomeEditor();
    void setHomeEditor(HomeEditor homeEditor);

    void setStatus(String status);
    void setName(String botName);

    void start(BotHome botHome);
    void stop(BotHome botHome);

    List<String> getChannels();
    Channel getChannel(String channelName) throws UserError;

    List<String> getRoles();
    String getRole(User user);
    boolean hasRole(User user, String role);
    boolean hasRole(String role);
    void clearRole(User user, String role) throws UserError;
    void setRole(User user, String role) throws UserError;

    List<String> clearRole(String role) throws UserError;
    boolean isHigherRanked(User user, User otherUser) throws UserError;

    boolean hasUser(String userName);
    User getUser(long id, String userName);
    User getUser(String userName) throws UserError;
    User getUser(HomedUser homedUser);

    Message getSavedMessage(long channelId, long messageId);
    Map<String, Emote> getEmoteMap();
    Emote getEmote(String emoteName);
    List<Emote> getEmotes();
    void updateEmotes();
}
