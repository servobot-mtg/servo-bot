package com.ryan_mtg.servobot.discord.bot;

import com.ryan_mtg.servobot.discord.commands.CommandTable;
import com.ryan_mtg.servobot.discord.events.CommandListener;
import com.ryan_mtg.servobot.discord.events.MultiDelegatingListener;
import com.ryan_mtg.servobot.discord.events.ReactionListener;
import com.ryan_mtg.servobot.discord.reaction.ReactionTable;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class BotHome {
    private String homeName;
    private int id;
    private Streamer streamer;
    private CommandTable commandTable;
    private ReactionTable reactionTable;
    private MultiDelegatingListener listener;

    public BotHome(final String homeName, final int id, final Streamer streamer, final CommandTable commandTable,
                   final ReactionTable reactionTable) {
        this.homeName = homeName;
        this.id = id;
        this.streamer = streamer;
        this.commandTable = commandTable;
        this.reactionTable = reactionTable;

        listener = new MultiDelegatingListener(new CommandListener(commandTable), new ReactionListener(reactionTable));
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

    public ListenerAdapter getListener() {
        return listener;
    }
}
