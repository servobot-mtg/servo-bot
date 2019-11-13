package com.ryan_mtg.servobot.events;

import com.ryan_mtg.servobot.model.User;
import com.ryan_mtg.servobot.reaction.Reaction;
import com.ryan_mtg.servobot.reaction.ReactionTable;
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

        String content = message.getContent();
        Home home = message.getHome();
        for (Reaction reaction : reactionTable) {
            if (reaction.matches(content)) {
                String emoteName = reaction.getEmoteName();
                Emote emote = home.getEmote(emoteName);
                if (emote != null) {
                    LOGGER.info("Adding a " + emoteName + " reaction to " + sender + "'s message.");
                    message.addEmote(emote);
                } else {
                    LOGGER.warn("Unable to find emote " + emoteName + " in " + home.getName());
                }
            }
        }
    }

    @Override
    public void onStreamStart(final StreamStartEvent streamStartEvent) { }

    @Override
    public void onAlert(final AlertEvent alertEvent) { }
}
