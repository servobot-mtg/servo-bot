package com.ryan_mtg.servobot.model;

import com.ryan_mtg.servobot.events.HomeDelegatingListener;

import java.util.ArrayList;
import java.util.List;

public class Bot {
    private List<BotHome> homes = new ArrayList<>();
    private HomeDelegatingListener listener = new HomeDelegatingListener();
    private List<Service> services;


    public Bot(final List<Service> services) {
        this.services = services;
    }

    public void addHome(final BotHome home) {
        homes.add(home);
        listener.register(home);

        for (Service service : services) {
            service.register(home);
        }
    }

    public void startBot() throws Exception {
        for (Service service : services) {
            service.start(listener);
        }
    }

    public List<BotHome> getHomes() {
        return homes;
    }

    public BotHome getHome(final String homeName) {
        for(BotHome home : homes) {
            if (home.getHomeName().equals(homeName)) {
                return home;
            }
        }
        return null;
    }
}
