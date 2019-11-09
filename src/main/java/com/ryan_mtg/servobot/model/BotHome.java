package com.ryan_mtg.servobot.model;

import com.ryan_mtg.servobot.commands.CommandTable;
import com.ryan_mtg.servobot.events.CommandListener;
import com.ryan_mtg.servobot.events.EventListener;
import com.ryan_mtg.servobot.events.MultiDelegatingListener;
import com.ryan_mtg.servobot.events.ReactionListener;
import com.ryan_mtg.servobot.reaction.ReactionTable;

import java.util.List;

public class BotHome {
    private String homeName;
    private int id;
    private CommandTable commandTable;
    private ReactionTable reactionTable;
    private EventListener eventListener;
    private List<ServiceHome> serviceHomes;

    public BotHome(final String homeName, final int id, final CommandTable commandTable,
                   final ReactionTable reactionTable, final List<ServiceHome> serviceHomes) {
        this.homeName = homeName;
        this.id = id;
        this.commandTable = commandTable;
        this.reactionTable = reactionTable;
        this.serviceHomes = serviceHomes;

        eventListener =
                new MultiDelegatingListener(new CommandListener(commandTable), new ReactionListener(reactionTable));
    }

    public String getHomeName() {
        return homeName;
    }

    public int getId() {
        return id;
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

    public List<ServiceHome> getServiceHomes() {
        return serviceHomes;
    }
}
