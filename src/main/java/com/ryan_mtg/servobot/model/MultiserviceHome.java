package com.ryan_mtg.servobot.model;

import com.ryan_mtg.servobot.discord.model.DiscordService;
import com.ryan_mtg.servobot.error.BotHomeError;
import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.twitch.model.TwitchService;
import com.ryan_mtg.servobot.user.HomedUser;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

public class MultiserviceHome implements ServiceHome {
    private Map<Integer, ServiceHome> serviceHomes;

    @Getter @Setter
    private HomeEditor homeEditor;

    public MultiserviceHome(final Map<Integer, ServiceHome> serviceHomes, final HomeEditor homeEditor) {
        this.serviceHomes = serviceHomes;
        this.homeEditor = homeEditor;
    }

    @Override
    public Service getService() {
        return null;
    }

    @Override
    public int getServiceType() {
        return Service.NO_SERVICE_TYPE;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getBotName() {
        return preferService(TwitchService.TYPE).getBotName();
    }

    @Override
    public String getLink() {
        return preferService(TwitchService.TYPE).getLink();
    }

    @Override
    public String getImageUrl() {
        return preferService(TwitchService.TYPE).getImageUrl();
    }

    @Override
    public String getDescription() {
        return preferService(TwitchService.TYPE).getDescription();
    }

    @Override
    public boolean isStreaming() {
        return preferService(TwitchService.TYPE).isStreaming();
    }

    @Override
    public void setStatus(final String status) {
        serviceHomes.values().forEach(serviceHome -> serviceHome.setStatus(status));
    }

    @Override
    public void setName(final String botName) {
        serviceHomes.values().forEach(serviceHome -> serviceHome.setName(botName));
    }

    @Override
    public boolean isStreamer(final User user) {
        return preferService(DiscordService.TYPE).isStreamer(user);
    }

    @Override
    public void start(final BotHome botHome) {}

    @Override
    public void stop(final BotHome botHome) {}

    @Override
    public List<Channel> getChannels() {
        return preferService(DiscordService.TYPE).getChannels();
    }

    @Override
    public Channel getChannel(final String channelName) throws UserError {
        return preferService(DiscordService.TYPE).getChannel(channelName);
    }

    @Override
    public Channel getChannel(final long channelId) throws BotHomeError {
        return preferService(DiscordService.TYPE).getChannel(channelId);
    }

    @Override
    public List<Role> getRoles() {
        return preferService(DiscordService.TYPE).getRoles();
    }

    @Override
    public String getRole(final User user) {
        return preferService(DiscordService.TYPE).getRole(user);
    }

    @Override
    public Role getRole(final long roleId) throws BotHomeError {
        return preferService(DiscordService.TYPE).getRole(roleId);
    }

    @Override
    public boolean hasRole(User user, long roleId) {
        return preferService(DiscordService.TYPE).hasRole(user, roleId);
    }

    @Override
    public void clearRole(final User user, final String role) throws UserError {
        preferService(DiscordService.TYPE).clearRole(user, role);
    }

    @Override
    public void clearRole(User user, long roleId) throws BotHomeError {
        preferService(DiscordService.TYPE).clearRole(user, roleId);
    }

    @Override
    public void setRole(final User user, final String role) throws UserError {
        preferService(DiscordService.TYPE).setRole(user, role);
    }

    @Override
    public void setRole(User user, long roleId) throws BotHomeError {
        preferService(DiscordService.TYPE).setRole(user, roleId);
    }

    @Override
    public List<String> clearRole(final String role) throws UserError {
        return preferService(DiscordService.TYPE).clearRole(role);
    }

    @Override
    public boolean isHigherRanked(final User user, final User otherUser) throws UserError {
        return preferService(DiscordService.TYPE).isHigherRanked(user, otherUser);
    }

    @Override
    public boolean hasUser(final String userName) {
        return preferService(DiscordService.TYPE).hasUser(userName);
    }

    @Override
    public User getUser(final long id, final String userName) {
        return preferService(DiscordService.TYPE).getUser(id, userName);
    }

    @Override
    public User getUser(final String userName) throws UserError {
        return preferService(DiscordService.TYPE).getUser(userName);
    }

    @Override
    public User getUser(final HomedUser homedUser) {
        return preferService(DiscordService.TYPE).getUser(homedUser);
    }

    @Override
    public void setNickName(final User user, final String nickName) {
        preferService(DiscordService.TYPE).setNickName(user, nickName);
    }

    @Override
    public Map<String, Emote> getEmoteMap() {
        return preferService(TwitchService.TYPE).getEmoteMap();
    }

    @Override
    public Emote getEmote(final String emoteName) {
        return preferService(DiscordService.TYPE).getEmote(emoteName);
    }

    @Override
    public List<Emote> getEmotes() {
        return preferService(DiscordService.TYPE).getEmotes();
    }

    @Override
    public void updateEmotes() {
        serviceHomes.values().forEach(serviceHome -> serviceHome.updateEmotes());
    }

    @Override
    public Message getSavedMessage(final long channelId, final long messageId) {
        return preferService(DiscordService.TYPE).getSavedMessage(channelId, messageId);
    }

    private ServiceHome preferService(final int serviceType) {
        if(serviceHomes.containsKey(serviceType)) {
            return serviceHomes.get(serviceType);
        }
        return serviceHomes.values().iterator().next();
    }
}
