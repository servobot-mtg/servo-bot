package com.ryan_mtg.servobot.events;

import com.ryan_mtg.servobot.commands.game_queue.GameQueueUtils;
import com.ryan_mtg.servobot.commands.hierarchy.Command;
import com.ryan_mtg.servobot.error.BotErrorHandler;
import com.ryan_mtg.servobot.model.ServiceHome;
import com.ryan_mtg.servobot.model.User;
import com.ryan_mtg.servobot.model.game_queue.GameQueue;
import com.ryan_mtg.servobot.model.game_queue.GameQueueTable;
import com.ryan_mtg.servobot.model.reaction.Reaction;
import com.ryan_mtg.servobot.model.reaction.ReactionTable;
import com.ryan_mtg.servobot.model.Emote;
import com.ryan_mtg.servobot.model.Message;
import com.ryan_mtg.servobot.model.roles.RoleTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReactionListener implements EventListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReactionListener.class);
    private final ReactionTable reactionTable;
    private final RoleTable roleTable;
    private final CommandPerformer commandPerformer;
    private final GameQueueTable gameQueueTable;

    public ReactionListener(final ReactionTable reactionTable, final RoleTable roleTable,
            final CommandPerformer commandPerformer, final GameQueueTable gameQueueTable) {
        this.reactionTable = reactionTable;
        this.commandPerformer = commandPerformer;
        this.gameQueueTable = gameQueueTable;
        this.roleTable = roleTable;
    }

    @Override
    public void onMessage(final MessageHomeEvent messageHomeEvent) {
        Message  message = messageHomeEvent.getMessage();
        User sender = messageHomeEvent.getSender();
        if (sender.isBot() || !message.canEmote()) {
            return;
        }

        ServiceHome serviceHome = messageHomeEvent.getServiceHome();
        for (Reaction reaction : reactionTable) {
            if (reaction.matches(message)) {
                String emoteName = reaction.getEmoteName();
                Emote emote = serviceHome.getEmote(emoteName);
                if (emote != null) {
                    LOGGER.info("Adding a {} reaction to {}'s message.", emoteName, sender.getName());
                    message.addEmote(emote);
                }

                for(Command command : reaction.getCommands()) {
                    commandPerformer.perform(messageHomeEvent, command);
                }
            }
        }
    }

    @Override
    public void onEmoteAdded(final EmoteHomeEvent emoteHomeEvent) {
        User reactor = emoteHomeEvent.getSender();
        if (reactor.isBot()) {
            return;
        }

        BotErrorHandler.handleError(() -> {
            GameQueue gameQueue = gameQueueTable.matchesQueue(emoteHomeEvent.getMessage());
            if (gameQueue != null) {
                BotErrorHandler.handleError(() -> GameQueueUtils.addEmote(emoteHomeEvent, gameQueue, reactor));
            }

            roleTable.onEmoteAdded(emoteHomeEvent);
        });
    }

    @Override
    public void onEmoteRemoved(final EmoteHomeEvent emoteHomeEvent) {
        User reactor = emoteHomeEvent.getSender();
        if (reactor.isBot()) {
            return;
        }

        BotErrorHandler.handleError(() -> {
            GameQueue gameQueue = gameQueueTable.matchesQueue(emoteHomeEvent.getMessage());
            if (gameQueue != null) {
                BotErrorHandler.handleError(() -> GameQueueUtils.removeEmote(emoteHomeEvent, gameQueue, reactor));
            }

            roleTable.onEmoteRemoved(emoteHomeEvent);
        });
    }

    @Override
    public void onPrivateMessage(final MessageEvent messageEvent) {}

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
