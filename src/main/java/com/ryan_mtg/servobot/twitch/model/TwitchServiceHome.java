package com.ryan_mtg.servobot.twitch.model;

import com.github.twitch4j.helix.domain.UserList;
import com.google.common.collect.Lists;
import com.ryan_mtg.servobot.error.SystemError;
import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.model.BotHome;
import com.ryan_mtg.servobot.model.Channel;
import com.ryan_mtg.servobot.model.Emote;
import com.ryan_mtg.servobot.model.HomeEditor;
import com.ryan_mtg.servobot.model.Service;
import com.ryan_mtg.servobot.model.ServiceHome;
import com.ryan_mtg.servobot.model.User;
import com.ryan_mtg.servobot.user.HomedUser;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.List;

public class TwitchServiceHome implements ServiceHome {
    private TwitchService twitchService;
    private long channelId;

    @Getter @Setter
    private HomeEditor homeEditor;

    private String channelName;
    private String imageUrl;

    public TwitchServiceHome(final TwitchService twitchService, final long channelId) {
        this.channelId = channelId;
        this.twitchService = twitchService;
    }

    @Override
    public Service getService() {
        return twitchService;
    }

    @Override
    public int getServiceType() {
        return TwitchService.TYPE;
    }

    @Override
    public String getName() {
        if (channelName != null) {
            return channelName;
        }
        return channelName = twitchService.fetchChannelName(channelId);
    }

    @Override
    public String getBotName() {
        return twitchService.getBotName();
    }

    @Override
    public String getLink() {
        return String.format("http://twitch.tv/%s", getName());
    }

    @Override
    public String getImageUrl() {
        if (imageUrl != null) {
            return imageUrl;
        }
        return imageUrl = twitchService.fetchChannelImageUrl(channelId);
    }

    @Override
    public String getDescription() {
        return String.format("Channel %s", getName());
    }

    @Override
    public boolean isStreaming() {
        return twitchService.isStreaming(channelId);
    }

    @Override
    public void setStatus(final String status) {}

    @Override
    public void setName(final String botName) {}

    @Override
    public boolean isStreamer(final User user) {
        return user.getHomedUser().isStreamer();
    }

    @Override
    public void start(final BotHome botHome) {
        twitchService.joinChannel(getName());
    }

    @Override
    public void stop(final BotHome botHome) {
        twitchService.leaveChannel(getName());
    }

    @Override
    public List<String> getChannels() {
        return Lists.newArrayList(getName());
    }

    @Override
    public Channel getChannel(final String channelName) throws UserError {
        if (channelName.equals(getName())) {
            return twitchService.getChannel(channelName);
        }
        throw new UserError("No Twitch channel named %s", channelName);
    }

    @Override
    public List<String> getRoles() {
        return Collections.emptyList();
    }

    @Override
    public String getRole(final User user) {
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
    public void clearRole(final User user, final String role) {
        throw new SystemError("Twitch doesn't allow setting roles");
    }

    @Override
    public void setRole(final User user, final String role) {
        throw new SystemError("Twitch doesn't allow setting roles");
    }

    @Override
    public List<String> clearRole(final String role) {
        throw new SystemError("Twitch doesn't allow setting roles");
    }

    @Override
    public boolean isHigherRanked(final User user, final User otherUser) {
        return false;
    }

    @Override
    public boolean hasUser(final String userName) {
        return false;
    }

    @Override
    public User getUser(final String userName) throws UserError {
        com.github.twitch4j.helix.domain.User user = twitchService.fetchUser(userName);
        if (user == null) {
            throw new UserError("No user %s", userName);
        }
        HomedUser homedUser = homeEditor.getUserByTwitchId(Integer.parseInt(user.getId()), user.getLogin());
        return new TwitchUser(homedUser);
    }

    @Override
    public User getUser(final HomedUser homedUser) {
        return new TwitchUser(homedUser);
    }

    @Override
    public Emote getEmote(final String emoteName) {
        return null;
    }

    @Override
    public List<Emote> getEmotes() {
        return Collections.emptyList();
    }

    public long getChannelId() {
        return channelId;
    }
}
