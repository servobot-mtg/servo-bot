package com.ryan_mtg.servobot.model;

import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.user.HomedUser;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MultiServiceHome implements Home {
    private Map<Integer, ServiceHome> serviceHomes;
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
        return  null;
    }

    @Override
    public boolean isStreamer(final User user) {
        return false;
    }

    @Override
    public String getRole(final User user, final int serviceType) {
        return null;
    }

    @Override
    public void setRole(final User user, final String role) {
    }

    @Override
    public void setRole(final String username, final String role) {
    }

    @Override
    public List<String> clearRole(final String role) {
        return Arrays.asList();
    }

    @Override
    public boolean isHigherRanked(final String userName, final User sender) {
        return false;
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
    public HomeEditor getHomeEditor() {
        return homeEditor;
    }

    @Override
    public void setStatus(final String status) {
        serviceHomes.values().forEach(serviceHome -> serviceHome.getHome().setStatus(status));
    }
}
