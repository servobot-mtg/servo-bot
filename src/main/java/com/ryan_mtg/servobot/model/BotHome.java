package com.ryan_mtg.servobot.model;

import com.ryan_mtg.servobot.commands.Command;
import com.ryan_mtg.servobot.commands.CommandTable;
import com.ryan_mtg.servobot.data.models.BotHomeRow;
import com.ryan_mtg.servobot.data.repositories.BotHomeRepository;
import com.ryan_mtg.servobot.events.CommandListener;
import com.ryan_mtg.servobot.events.EventListener;
import com.ryan_mtg.servobot.events.MultiDelegatingListener;
import com.ryan_mtg.servobot.events.ReactionListener;
import com.ryan_mtg.servobot.model.alerts.AlertGenerator;
import com.ryan_mtg.servobot.reaction.Reaction;
import com.ryan_mtg.servobot.reaction.ReactionTable;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;

public class BotHome {
    private int id;
    private Bot parentBot;
    private String name;
    private String timeZone;
    private CommandTable commandTable;
    private ReactionTable reactionTable;
    private EventListener eventListener;
    private Map<Integer, ServiceHome> serviceHomes;

    public BotHome(final int id, final Bot parentBot, final String name, final String timeZone,
                   final CommandTable commandTable, final ReactionTable reactionTable,
                   final Map<Integer, ServiceHome> serviceHomes) {
        this.id = id;
        this.parentBot = parentBot;
        this.name = name;
        this.timeZone = timeZone;
        this.commandTable = commandTable;
        this.reactionTable = reactionTable;
        this.serviceHomes = serviceHomes;

        reactionTable.setTimeZone(timeZone);
        commandTable.setTimeZone(timeZone);
        eventListener =
                new MultiDelegatingListener(new CommandListener(commandTable), new ReactionListener(reactionTable));
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
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

    @Transactional
    public void setTimeZone(final String timeZone) {
        this.timeZone = timeZone;
        reactionTable.setTimeZone(timeZone);
        commandTable.setTimeZone(timeZone);

        parentBot.getAlertQueue().update(this);

        BotHomeRepository botHomeRepository = parentBot.getSerializers().getBotHomeRepository();
        BotHomeRow botHomeRow = botHomeRepository.findById(id);
        botHomeRow.setTimeZone(timeZone);
        botHomeRepository.save(botHomeRow);
    }

    public boolean secureCommand(int commandId, boolean secure) {
        Command command = commandTable.secureCommand(commandId, secure);
        parentBot.getSerializers().getCommandSerializer().saveCommand(id, command);
        return command.isSecure();
    }

    public boolean secureReaction(int reactionId, boolean secure) {
        Reaction reaction = reactionTable.secureReaction(reactionId, secure);
        parentBot.getSerializers().getReactionSerializer().saveReaction(id, reaction);
        return reaction.isSecure();
    }
}
