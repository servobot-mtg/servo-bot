package com.ryan_mtg.servobot.model;

import com.ryan_mtg.servobot.error.BotHomeError;
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

    List<Channel> getChannels();
    Channel getChannel(String channelName) throws UserError;
    Channel getChannel(long channelId) throws BotHomeError;

    List<Role> getRoles();
    String getRole(User user);
    Role getRole(long roleId) throws BotHomeError;
    boolean hasRole(User user, long roleId);
    void clearRole(User user, String role) throws UserError;
    void clearRole(User user, long roleId) throws BotHomeError;
    void setRole(User user, String role) throws UserError;
    void setRole(User user, long roleId) throws BotHomeError;

    List<String> clearRole(String role) throws UserError;
    boolean isHigherRanked(User user, User otherUser) throws UserError;

    boolean hasUser(String userName);
    User getUser(long serviceId, String userName);
    User getUser(String userName) throws UserError;
    User getUser(HomedUser homedUser);
    void setNickName(User user, String nickName);

    Message getSavedMessage(long channelId, long messageId);
    Map<String, Emote> getEmoteMap();
    Emote getEmote(String emoteName);
    List<Emote> getEmotes();
    void updateEmotes();
}
