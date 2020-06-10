package com.ryan_mtg.servobot.twitch.model;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.chat.TwitchChat;
import com.github.twitch4j.helix.domain.StreamList;
import com.github.twitch4j.helix.domain.UserList;
import com.ryan_mtg.servobot.error.SystemError;
import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.model.Channel;
import com.ryan_mtg.servobot.model.Emote;
import com.ryan_mtg.servobot.model.Home;
import com.ryan_mtg.servobot.model.HomeEditor;
import com.ryan_mtg.servobot.model.ServiceHome;
import com.ryan_mtg.servobot.model.User;
import com.ryan_mtg.servobot.user.HomedUser;

import java.util.Collections;
import java.util.List;

public class TwitchChannel implements Channel, Home {
    private TwitchClient twitchClient;
    private TwitchChat twitchChat;
    private String channelName;
    private HomeEditor homeEditor;

    public TwitchChannel(final TwitchClient twitchClient, final String channelName, final HomeEditor homeEditor) {
        this.twitchClient = twitchClient;
        this.twitchChat = twitchClient.getChat();
        this.channelName = channelName;
        this.homeEditor = homeEditor;
    }

    @Override
    public void say(final String message) {
        if (!message.isEmpty()) {
            twitchChat.sendMessage(channelName, message);
        }
    }

    @Override
    public String getName() {
        return channelName;
    }

    @Override
    public String getBotName() {
        return homeEditor.getService(TwitchService.TYPE).getBotName();
    }

    @Override
    public ServiceHome getServiceHome(int serviceType) {
        return null;
    }

    @Override
    public Channel getChannel(final String channelName, final int serviceType) {
        if (serviceType != TwitchService.TYPE) {
            return null;
        }
        if (channelName.equals(getName())) {
            return this;
        }
        throw new IllegalArgumentException(channelName + " is not a channel for " + getName());
    }

    @Override
    public boolean isStreamer(final User user) {
        return user.getName().toLowerCase().equals(getName());
    }

    @Override
    public boolean isStreaming() {
        StreamList streamList = twitchClient.getHelix().getStreams(null, "", null, null,null, null, null,
                null, Collections.singletonList(channelName)).execute();
        return !streamList.getStreams().isEmpty();
    }

    @Override
    public String getRole(final User user, final int serviceType) {
        return "Unable to determine";
    }

    @Override
    public boolean hasRole(final User user, final String role) {
        return false;
    }

    @Override
    public boolean hasRole(final String role) {
        return false;
    }

    @Override
    public void clearRole(final User user, final String role) {}

    @Override
    public void setRole(final User user, final String role) {
        throw new SystemError("Twitch doesn't have roles");
    }

    @Override
    public List<String> clearRole(final String role) {
        throw new SystemError("Twitch doesn't have roles");
    }

    @Override
    public boolean isHigherRanked(final User user, final User otherUser) {
        return false;
    }

    @Override
    public boolean hasUser(String userName) {
        return false;
    }

    @Override
    public User getUser(final String userName) throws UserError {
        UserList userList = twitchClient.getHelix().
                getUsers(null, null, Collections.singletonList(userName)).execute();
        if (userList.getUsers().isEmpty()) {
            throw new UserError("No user %s", userName);
        }
        com.github.twitch4j.helix.domain.User user = userList.getUsers().get(0);
        HomedUser homedUser = homeEditor.getUserByTwitchId(Integer.parseInt(user.getId()), user.getLogin());
        return new TwitchUser(twitchChat, homedUser);
    }

    @Override
    public Emote getEmote(final String emoteName) {
        return null;
    }

    @Override
    public User getUser(final HomedUser homedUser) {
        return new TwitchUser(twitchChat, homedUser);
    }

    @Override
    public HomeEditor getHomeEditor() {
        return homeEditor;
    }

    @Override
    public void setStatus(final String status) {}
}
