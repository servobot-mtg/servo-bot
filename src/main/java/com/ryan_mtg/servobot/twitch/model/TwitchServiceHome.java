package com.ryan_mtg.servobot.twitch.model;

import com.google.common.collect.Lists;
import com.ryan_mtg.servobot.error.BotHomeError;
import com.ryan_mtg.servobot.error.SystemError;
import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.model.BotHome;
import com.ryan_mtg.servobot.model.Channel;
import com.ryan_mtg.servobot.model.Emote;
import com.ryan_mtg.servobot.model.HomeEditor;
import com.ryan_mtg.servobot.model.Message;
import com.ryan_mtg.servobot.model.Role;
import com.ryan_mtg.servobot.model.Service;
import com.ryan_mtg.servobot.model.ServiceHome;
import com.ryan_mtg.servobot.model.User;
import com.ryan_mtg.servobot.twitch.betterttv.BetterTtvClient;
import com.ryan_mtg.servobot.twitch.betterttv.json.EmoteJson;
import com.ryan_mtg.servobot.twitch.betterttv.json.EmotesJson;
import com.ryan_mtg.servobot.twitch.twitchemotes.TwitchEmotesClient;
import com.ryan_mtg.servobot.twitch.twitchemotes.json.TwitchEmotes;
import com.ryan_mtg.servobot.user.HomedUser;
import com.ryan_mtg.servobot.utility.Assertion;
import feign.FeignException;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TwitchServiceHome implements ServiceHome {
    private static TwitchEmotesClient twitchEmotesClient;
    private static BetterTtvClient bttvEmotesClient;

    private TwitchService twitchService;
    private long channelId;

    @Getter @Setter
    private HomeEditor homeEditor;

    private String channelName;
    private String imageUrl;
    private List<Emote> cachedEmotes;

    public TwitchServiceHome(final TwitchService twitchService, final long channelId) {
        Assertion.assertNotNull(twitchService, "TwitchService");

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
    public List<Channel> getChannels() {
        return Lists.newArrayList(twitchService.getChannel(this, channelId));
    }

    @Override
    public Channel getChannel(final String channelName) throws UserError {
        if (channelName.equals(getName())) {
            return twitchService.getChannel(this, channelId);
        }
        throw new UserError("No Twitch channel named %s", channelName);
    }

    @Override
    public Channel getChannel(final long channelId) throws BotHomeError {
        if (this.channelId == channelId) {
            return twitchService.getChannel(this, channelId);
        }
        throw new BotHomeError("No Twitch channel with id %d", channelId);
    }

    @Override
    public List<Role> getRoles() {
        return Collections.emptyList();
    }

    @Override
    public String getRole(final User user) {
        return "Unable to determine";
    }

    @Override
    public Role getRole(final long roleId) {
        throw new SystemError("Twitch doesn't have roles");
    }

    @Override
    public boolean hasRole(User user, long roleId) {
        return false;
    }

    @Override
    public void clearRole(final User user, final String role) {
        throw new SystemError("Twitch doesn't allow setting roles");
    }

    @Override
    public void clearRole(User user, long roleId) {
        throw new SystemError("Twitch doesn't allow setting roles");
    }

    @Override
    public void setRole(final User user, final String role) {
        throw new SystemError("Twitch doesn't allow setting roles");
    }

    @Override
    public void setRole(User user, long roleId) {
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
    public User getUser(final long id, final String userName) {
        HomedUser homedUser = homeEditor.getUserByTwitchId((int) id, userName);
        return new TwitchUser(homedUser);
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
    public void setNickName(final User user, final String nickName) {
        throw new SystemError("Twitch doesn't allow setting nicknames");
    }

    @Override
    public Message getSavedMessage(final long channelId, final long messageId) {
        return null;
    }

    @Override
    public Map<String, Emote> getEmoteMap() {
        return homeEditor.getEmoteMap(TwitchService.TYPE);
    }

    @Override
    public Emote getEmote(final String emoteName) {
        return null;
    }

    @Override
    public List<Emote> getEmotes() {
        if (cachedEmotes == null) {
            updateEmotes();
        }
        return cachedEmotes;
    }

    @Override
    public void updateEmotes() {
        TwitchEmotesClient twitchEmotesClient = getTwitchEmotesClient();
        List<Emote> emotes = new ArrayList<>();
        try {
            TwitchEmotes twitchEmotes = twitchEmotesClient.getChannelEmotes(channelId);
            for (com.ryan_mtg.servobot.twitch.twitchemotes.json.Emote emoteJson : twitchEmotes.getEmotes()) {
                emotes.add(new TwitchEmote(emoteJson.getCode(), emoteJson.getId()));
            }
        } catch (FeignException.NotFound e) {
            // Intentionally ignore, because the emote list should be empty.
        }

        BetterTtvClient bttvEmotesClient = getBttvEmotesClient();
        try {
            EmotesJson bttvEmotes = bttvEmotesClient.getChannelEmotes(channelId);
            for (EmoteJson emoteJson : bttvEmotes.getChannelEmotes()) {
                emotes.add(new BttvEmote(emoteJson.getCode(), emoteJson.getId()));
            }

            for (EmoteJson emoteJson : bttvEmotes.getSharedEmotes()) {
                emotes.add(new BttvEmote(emoteJson.getCode(), emoteJson.getId()));
            }
        } catch (FeignException.NotFound e) {
            // Intentionally ignore, because the emote list should be empty.
        }
        cachedEmotes = emotes;
    }

    public long getChannelId() {
        return channelId;
    }

    private static TwitchEmotesClient getTwitchEmotesClient() {
        if (twitchEmotesClient != null) {
            return twitchEmotesClient;
        }
        return twitchEmotesClient = TwitchEmotesClient.newClient();
    }

    private static BetterTtvClient getBttvEmotesClient() {
        if (bttvEmotesClient != null) {
            return bttvEmotesClient;
        }
        return bttvEmotesClient = BetterTtvClient.newClient();
    }
}
