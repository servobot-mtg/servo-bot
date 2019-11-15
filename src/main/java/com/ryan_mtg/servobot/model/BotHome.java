package com.ryan_mtg.servobot.model;

import com.ryan_mtg.servobot.commands.CommandTable;
import com.ryan_mtg.servobot.events.CommandListener;
import com.ryan_mtg.servobot.events.EventListener;
import com.ryan_mtg.servobot.events.MultiDelegatingListener;
import com.ryan_mtg.servobot.events.ReactionListener;
import com.ryan_mtg.servobot.model.alerts.AlertGenerator;
import com.ryan_mtg.servobot.reaction.ReactionTable;

import java.util.List;
import java.util.Map;

public class BotHome {
    private int id;
    private String timeZone;
    private CommandTable commandTable;
    private ReactionTable reactionTable;
    private EventListener eventListener;
    private Map<Integer, ServiceHome> serviceHomes;

    public BotHome(final int id, final String timeZone, final CommandTable commandTable,
                   final ReactionTable reactionTable, final Map<Integer, ServiceHome> serviceHomes) {
        this.timeZone = timeZone;
        this.id = id;
        this.commandTable = commandTable;
        this.reactionTable = reactionTable;
        this.serviceHomes = serviceHomes;

        reactionTable.setTimeZone(timeZone);
        eventListener =
                new MultiDelegatingListener(new CommandListener(commandTable), new ReactionListener(reactionTable));
    }

    public int getId() {
        return id;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public CommandTable getCommandTable() {
        return commandTable;
    }

    public ReactionTable getReactionTable() {
        return reactionTable;
    }

    public EventListener getListener() {
        return eventListener;
    }

    public Map<Integer, ServiceHome> getServiceHomes() {
        return serviceHomes;
    }

    public ServiceHome getServiceHome(final int serviceType) {
        return serviceHomes.get(serviceType);
    }

    public List<AlertGenerator> getAlertGenerators() {
        return commandTable.getAlertGenerators();
    }
}
