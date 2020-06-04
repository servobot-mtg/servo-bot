package com.ryan_mtg.servobot.events;

import com.ryan_mtg.servobot.commands.hierarchy.Command;
import com.ryan_mtg.servobot.model.User;
import com.ryan_mtg.servobot.model.reaction.Reaction;
import com.ryan_mtg.servobot.model.reaction.ReactionTable;
import com.ryan_mtg.servobot.model.Emote;
import com.ryan_mtg.servobot.model.Home;
import com.ryan_mtg.servobot.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReactionListener implements EventListener {
    private static Logger LOGGER = LoggerFactory.getLogger(ReactionListener.class);
    private final ReactionTable reactionTable;
    private final CommandPerformer commandPerformer;

    public ReactionListener(final ReactionTable reactionTable, final CommandPerformer commandPerformer) {
        this.reactionTable = reactionTable;
        this.commandPerformer = commandPerformer;
    }

    @Override
    public void onMessage(final MessageHomeEvent messageHomeEvent) {
        Message  message = messageHomeEvent.getMessage();
        User sender = messageHomeEvent.getSender();
        if (sender.isBot() || !message.canEmote()) {
            return;
        }

        Home home = messageHomeEvent.getHome();
        for (Reaction reaction : reactionTable) {
            if (reaction.matches(message)) {
                String emoteName = reaction.getEmoteName();
                Emote emote = home.getEmote(emoteName);
                if (emote != null) {
                    LOGGER.info("Adding a " + emoteName + " reaction to " + sender + "'s message.");
                    message.addEmote(emote);
                }

                for(Command command : reaction.getCommands()) {
                    commandPerformer.perform(messageHomeEvent, command);
                }
            }
        }
    }

    @Override
    public void onStreamStart(final StreamStartEvent streamStartEvent) {}

    @Override
    public void onNewUser(final UserHomeEvent newUserEvent) {}

    @Override
    public void onRaid(final UserHomeEvent raidEvent) {}

    @Override
    public void onSubscribe(UserHomeEvent subscribeEvent) {}

    @Override
    public void onAlert(final AlertEvent alertEvent) {}
}
