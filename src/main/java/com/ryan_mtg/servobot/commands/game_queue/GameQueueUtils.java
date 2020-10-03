package com.ryan_mtg.servobot.commands.game_queue;

import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.events.EmoteHomeEvent;
import com.ryan_mtg.servobot.model.Message;
import com.ryan_mtg.servobot.model.User;
import com.ryan_mtg.servobot.model.game_queue.GameQueue;
import com.ryan_mtg.servobot.user.HomedUser;

import java.util.List;

public class GameQueueUtils {
    public static final String DAGGER_EMOTE = "🗡️";

    public static void addEmote(final EmoteHomeEvent event, final GameQueue gameQueue, final User reactor) {
        if (event.getEmote().getName().equals(DAGGER_EMOTE)) {
            try {
                gameQueue.enqueue(reactor.getHomedUser());
            } catch (UserError e) {
                return;
            }

            updateMessage(gameQueue, event.getMessage());
        }
    }

    public static void removeEmote(final EmoteHomeEvent event, final GameQueue gameQueue, final User reactor) {
        if (event.getEmote().getName().equals(DAGGER_EMOTE)) {
            try {
                gameQueue.dequeue(reactor.getHomedUser());
            } catch (UserError e) {
                return;
            }

            updateMessage(gameQueue, event.getMessage());
        }
    }

    public static void updateMessage(final GameQueue gameQueue, final Message message) {
        String text = createMessage(gameQueue);
        message.updateText(text);
    }

    public static String createMessage(final GameQueue gameQueue) {
        StringBuilder text = new StringBuilder();
        text.append("Game Queue for ").append(gameQueue.getGame().getName());

        text.append("\t\t\t");
        appendCode(text, gameQueue.getCode(), gameQueue.getServer());
        text.append("\n\n");

        appendPlayerList(text, gameQueue.getGamePlayers(), "CSS", "Players", "No active game.");
        text.append('\n');
        appendPlayerList(text, gameQueue.getWaitQueue(), "HTTP", "Queue", "No one is waiting.");

        text.append("To join the queue, react with " + DAGGER_EMOTE);
        return text.toString();
    }

    public static String getCodeMessage(final GameQueue gameQueue) {
        return getCodeMessage(gameQueue.getCode(), gameQueue.getServer());
    }

    public static String getCodeMessage(final String code, final String server) {
        StringBuilder text = new StringBuilder();
        if (code != null) {
            text.append("The code is ");
        } else if (server != null) {
            text.append("The server is ");
        }
        appendCode(text, code, server);
        text.append('.');
        return text.toString();
    }

    public static void appendCode(final StringBuilder text, final String code, final String server) {
        if (code != null) {
            text.append("🔑 **").append(code).append("**");
            if (server != null) {
                text.append(" on 🖥️ ").append(server);
            }
        } else if (server != null) {
            text.append("🖥️ ").append(server);
        } else {
            text.append("No code set");
        }
    }

    public static String getPlayersQueuedMessage(final List<HomedUser> players) {
        return getPlayersMessage("Added", "to", players);
    }

    public static String getPlayersDequeuedMessage(final List<HomedUser> players) {
        return getPlayersMessage("Removed", "from", players);
    }

    private static String getPlayersMessage(final String action, final String dir, final List<HomedUser> players) {
        StringBuilder text = new StringBuilder();
        text.append(action).append(' ');
        for (int i = 0; i < players.size(); i++) {
            text.append(players.get(i).getName());
            if (i + 2 < players.size()) {
                text.append(", ");
            } else if (i + 1 < players.size()) {
                if (players.size() == 2) {
                    text.append(" and ");
                } else {
                    text.append(", and ");
                }
            }
        }
        text.append(' ').append(dir).append(" the queue.");
        return text.toString();
    }

    private static void appendPlayerList(final StringBuilder text, final List<HomedUser> players, final String syntax,
            final String title, final String emptyMessage) {
        if (players.isEmpty()) {
            text.append(emptyMessage).append('\n');
        } else {
            text.append(title).append(" ```").append(syntax).append('\n');
            for (HomedUser user : players) {
                text.append(user.getName()).append('\n');
            }
            text.append("```\n");
        }
    }
}
