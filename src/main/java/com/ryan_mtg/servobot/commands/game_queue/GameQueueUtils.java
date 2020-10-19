package com.ryan_mtg.servobot.commands.game_queue;

import com.ryan_mtg.servobot.error.BotHomeError;
import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.events.EmoteHomeEvent;
import com.ryan_mtg.servobot.events.MessageEvent;
import com.ryan_mtg.servobot.model.Message;
import com.ryan_mtg.servobot.model.User;
import com.ryan_mtg.servobot.model.editors.GameQueueEditor;
import com.ryan_mtg.servobot.model.game_queue.GameQueue;
import com.ryan_mtg.servobot.model.game_queue.GameQueueAction;
import com.ryan_mtg.servobot.user.HomedUser;
import com.ryan_mtg.servobot.utility.Strings;

import java.util.List;
import java.util.function.Predicate;

public class GameQueueUtils {
    public static final String REFRESH_EMOTE = "🔄";
    public static final String DAGGER_EMOTE = "🗡️";
    public static final String READY_EMOTE = "👋";
    public static final String LG_EMOTE = "😴";
    public static final String LEAVE_EMOTE = "🏠";

    public static void addEmote(final EmoteHomeEvent event, final GameQueue gameQueue, final User reactor) {
        try {
            GameQueueEditor gameQueueEditor = event.getGameQueueEditor();
            String emoteName = event.getEmote().getName();
            if (emoteName.equals(DAGGER_EMOTE)) {
                GameQueueAction action = gameQueueEditor.addUser(gameQueue.getId(), reactor.getHomedUser());
                updateMessage(event, gameQueue, event.getMessage(), action, false);
            } else if (emoteName.equals(REFRESH_EMOTE)) {
                updateMessage(event, gameQueue, event.getMessage(), GameQueueAction.emptyAction(), false);
            } else if (emoteName.equals(READY_EMOTE)) {
                GameQueueAction action = gameQueueEditor.readyUser(gameQueue.getId(), reactor.getHomedUser());
                updateMessage(event, gameQueue, event.getMessage(), action, false);
            } else if (emoteName.equals(LG_EMOTE)) {
                GameQueueAction action = gameQueueEditor.lgUser(gameQueue.getId(), reactor.getHomedUser());
                updateMessage(event, gameQueue, event.getMessage(), action, false);
            } else if (emoteName.equals(LEAVE_EMOTE)) {
                GameQueueAction action = gameQueueEditor.dequeueUser(gameQueue.getId(), reactor.getHomedUser());
                updateMessage(event, gameQueue, event.getMessage(), action, false);
            }
        } catch (UserError | BotHomeError e) {
        }
    }

    public static void removeEmote(final EmoteHomeEvent event, final GameQueue gameQueue, final User reactor) {
        try {
            GameQueueEditor gameQueueEditor = event.getGameQueueEditor();
            String emoteName = event.getEmote().getName();
            if (emoteName.equals(DAGGER_EMOTE)) {
                GameQueueAction action = gameQueueEditor.dequeueUser(gameQueue.getId(), reactor.getHomedUser());
                updateMessage(event, gameQueue, event.getMessage(), action, false);
            } else if (emoteName.equals(REFRESH_EMOTE)) {
                updateMessage(event, gameQueue, event.getMessage(), GameQueueAction.emptyAction(), false);
            }
        } catch (UserError | BotHomeError e) {
        }
    }

    public static void updateMessage(final MessageEvent event, final GameQueue gameQueue, final Message message,
            final GameQueueAction action, final boolean verbose) throws BotHomeError {
        String text = createMessage(gameQueue);
        message.updateText(text);
        respondToAction(event, action, verbose);
    }

    public static void respondToAction(final MessageEvent event, final GameQueueAction action, final boolean verbose)
            throws BotHomeError {
        String response = "";
        if (!action.getQueuedPlayers().isEmpty() && verbose) {
            response = combine(response, GameQueueUtils.getPlayersQueuedMessage(action.getQueuedPlayers()));
        }

        if (!action.getDequeuedPlayers().isEmpty() && verbose) {
            response = combine(response, GameQueueUtils.getPlayersDequeuedMessage(action.getDequeuedPlayers()));
        }

        if (!action.getReadiedPlayers().isEmpty() && verbose) {
            response = combine(response, GameQueueUtils.getPlayersReadyMessage(action.getReadiedPlayers()));
        }

        if (!action.getLgedPlayers().isEmpty() && verbose) {
            response = combine(response, GameQueueUtils.getPlayersLgedMessage(action.getLgedPlayers()));
        }

        if (!action.getOnDeckedPlayers().isEmpty()) {
            response = combine(response, GameQueueUtils.getPlayersOnDeckedMessage(action.getOnDeckedPlayers()));
        }

        if ((action.getCode() != null || action.getServer() != null) && verbose) {
            response = combine(response, GameQueueUtils.getCodeMessage(action.getCode(), action.getServer()));
        }

        if (!Strings.isBlank(response)) {
            event.say(response);
        }
    }

    public static String createMessage(final GameQueue gameQueue) {
        StringBuilder text = new StringBuilder();
        text.append("Game Queue for ").append(gameQueue.getGame().getName());

        text.append("\t\t\t");
        appendCode(text, gameQueue.getCode(), gameQueue.getServer());
        text.append("\n\n");

        appendPlayerList(text, gameQueue.getGamePlayers(), "CSS", "Players", "No active game.", '#',
                homedUser -> gameQueue.isLg(homedUser));

        List<HomedUser> onDeck = gameQueue.getOnDeck();
        if (!onDeck.isEmpty()) {
            appendPlayerList(text, onDeck, "Diff", "On Deck", "No one is waiting.", '-', null);
        }

        List<HomedUser> waitQueue = gameQueue.getWaitQueue();
        if (!waitQueue.isEmpty() || onDeck.isEmpty()) {
            appendPlayerList(text, gameQueue.getWaitQueue(), "HTTP", "Queue", "No one is waiting.", '#', null);
        }

        text.append("React with:\n");
        text.append(DAGGER_EMOTE + ": To join the queue \t\t" + READY_EMOTE + ": To join game when on deck\n");
        text.append(LG_EMOTE + ": When it's your LG \t\t" + LEAVE_EMOTE + ": To leave the game and queue\n");
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
        return getPlayersMessage("Added", "to the queue.", players);
    }

    public static String getPlayersDequeuedMessage(final List<HomedUser> players) {
        return getPlayersMessage("Removed", "from the queue.", players);
    }

    public static String getPlayersLgedMessage(final List<HomedUser> players) {
        return getPlayersMessage("Marked ", "as LG.", players);
    }

    public static String getPlayersReadyMessage(final List<HomedUser> players) {
        return getPlayersMessage(null, (players.size() > 1 ? "are": "is") + " ready to play.", players);
    }

    public static String getPlayersOnDeckedMessage(final List<HomedUser> players) {
        StringBuilder text = new StringBuilder();
        appendPlayerList(text, players, true);
        if (players.size() > 1) {
            text.append(" are");
        } else {
            text.append(" is");
        }
        text.append(" on deck and should react to the queue with " + READY_EMOTE + " when they are ready to play!");
        return text.toString();
    }

    private static String getPlayersMessage(final String action, final String message, final List<HomedUser> players) {
        StringBuilder text = new StringBuilder();
        if (action != null) {
            text.append(action).append(' ');
        }
        appendPlayerList(text, players, false);
        text.append(' ').append(message);
        return text.toString();
    }

    private static void appendPlayerList(final StringBuilder text, final List<HomedUser> players, final boolean tag) {
        for (int i = 0; i < players.size(); i++) {
            if (tag) {
                text.append('@');
            }
            text.append(players.get(i).getDiscordUsername());
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
    }

    private static void appendPlayerList(final StringBuilder text, final List<HomedUser> players, final String syntax,
            final String title, final String emptyMessage, final char playerPrefix, final Predicate<HomedUser> isLg) {
        if (players.isEmpty()) {
            text.append(emptyMessage).append("\n\n");
        } else {
            text.append(title).append(" ```").append(syntax).append('\n');
            int count = 1;
            for (HomedUser user : players) {
                if (playerPrefix == '#') {
                    text.append(count++).append(") ");
                } else if (playerPrefix == '-') {
                    text.append("- ");
                }
                text.append(user.getName());
                if (isLg != null && isLg.test(user)) {
                    text.append(" (LG " + LG_EMOTE + ")");
                }
                text.append('\n');
            }
            text.append("```\n");
        }
    }

    private static String combine(final String a, final String b) {
        if (Strings.isBlank(a)) {
            return b;
        }
        return a + " " + b;
    }
}
