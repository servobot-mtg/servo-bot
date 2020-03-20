package com.ryan_mtg.servobot.events;

import com.ryan_mtg.servobot.commands.Command;
import com.ryan_mtg.servobot.commands.MessageCommand;
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

    public ReactionListener(final ReactionTable reactionTable) {
        this.reactionTable = reactionTable;
    }

    @Override
    public void onMessage(final MessageSentEvent messageSentEvent) {
        Message  message = messageSentEvent.getMessage();
        User sender = messageSentEvent.getSender();
        if (sender.isBot() || !message.canEmote()) {
            return;
        }

        Home home = message.getHome();
        for (Reaction reaction : reactionTable) {
            if (reaction.matches(message)) {
                String emoteName = reaction.getEmoteName();
                Emote emote = home.getEmote(emoteName);
                if (emote != null) {
                    LOGGER.info("Adding a " + emoteName + " reaction to " + sender + "'s message.");
                    message.addEmote(emote);
                }

                for(Command command : reaction.getCommands()) {
                    if (command instanceof MessageCommand) {
                        MessageCommand messageCommand = (MessageCommand) command;
                        try {
                            messageCommand.perform(messageSentEvent, null);
                        } catch (BotErrorException e) {
                            LOGGER.warn("Ignoring exception: " + e.getErrorMessage());
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onStreamStart(final StreamStartEvent streamStartEvent) {}

    @Override
    public void onNewUser(final UserEvent newUserEvent) {}

    @Override
    public void onRaid(final UserEvent raidEvent) {}

    @Override
    public void onSubscribe(UserEvent subscribeEvent) {}

    @Override
    public void onAlert(final AlertEvent alertEvent) {}
}
