package com.ryan_mtg.servobot.model;

import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.user.HomedUser;
import lombok.Getter;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MultiServiceHome implements Home {
    private Map<Integer, ServiceHome> serviceHomes;

    @Getter
    private HomeEditor homeEditor;

    public MultiServiceHome(final Map<Integer, ServiceHome> serviceHomes, final HomeEditor homeEditor) {
        this.serviceHomes = serviceHomes;
        this.homeEditor = homeEditor;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Channel getChannel(final String channelName, final int serviceType) {
        ServiceHome serviceHome = serviceHomes.get(serviceType);

        if (serviceHome != null) {
            return serviceHome.getHome().getChannel(channelName, serviceType);
        }
        return null;
    }

    @Override
    public boolean isStreamer(final User user) {
        return false;
    }

    @Override
    public boolean isStreaming() {
        return serviceHomes.values().stream().anyMatch(serviceHome -> serviceHome.isStreaming());
    }

    @Override
    public String getRole(final User user, final int serviceType) {
        return null;
    }

    @Override
    public boolean hasRole(final User user, final String role) {
        return false;
    }

    @Override
    public void clearRole(final User user, final String prisonRole) {

    }

    @Override
    public void setRole(final User user, final String role) {
    }

    @Override
    public List<String> clearRole(final String role) {
        return Collections.emptyList();
    }

    @Override
    public boolean isHigherRanked(User user, User otherUser) {
        return false;
    }

    @Override
    public User getUser(String userName) throws BotErrorException {
        return null;
    }

    @Override
    public Emote getEmote(final String emoteName) {
        return null;
    }

    @Override
    public User getUser(final HomedUser homedUser) {
        return null;
    }

    @Override
    public void setStatus(final String status) {
        serviceHomes.values().forEach(serviceHome -> serviceHome.getHome().setStatus(status));
    }
}
