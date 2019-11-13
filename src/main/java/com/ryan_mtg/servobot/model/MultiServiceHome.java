package com.ryan_mtg.servobot.model;

import java.util.Map;

public class MultiServiceHome implements Home {
    private Map<Integer, Service> services;
    private Map<Integer, ServiceHome> serviceHomes;

    public MultiServiceHome(final Map<Integer, Service> services, final Map<Integer, ServiceHome> serviceHomes) {
        this.services = services;
        this.serviceHomes = serviceHomes;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Channel getChannel(final String channelName, final int serviceType) {
        Service service = services.get(serviceType);
        ServiceHome serviceHome = serviceHomes.get(serviceType);

        if (service != null && serviceHome != null) {
            return service.getHome(serviceHome).getChannel(channelName, serviceType);
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
    public Emote getEmote(final String emoteName) {
        return null;
    }
}
