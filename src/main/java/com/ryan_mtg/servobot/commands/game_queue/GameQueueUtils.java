package com.ryan_mtg.servobot.commands.game_queue;

import com.ryan_mtg.servobot.error.BotHomeError;
import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.events.EmoteHomeEvent;
import com.ryan_mtg.servobot.events.MessageHomeEvent;
import com.ryan_mtg.servobot.model.Message;
import com.ryan_mtg.servobot.model.User;
import com.ryan_mtg.servobot.model.editors.GameQueueEditor;
import com.ryan_mtg.servobot.model.game_queue.Game;
import com.ryan_mtg.servobot.model.game_queue.GameQueue;
import com.ryan_mtg.servobot.model.game_queue.GameQueueAction;
import com.ryan_mtg.servobot.user.HomedUser;
import com.ryan_mtg.servobot.utility.Strings;
import com.ryan_mtg.servobot.utility.Time;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

public class GameQueueUtils {
    public static final String ROTATE_EMOTE = "🔄";
    public static final String DAGGER_EMOTE = "🗡️";
    public static final String READY_EMOTE = "👋";
    public static final String LG_EMOTE = "😴";
    public static final String LEAVE_EMOTE = "🏠";
    public static final String STREAMING_EMOTE = "📺";
    public static final String ON_CALL_EMOTE = "📞";

    public static void addEmote(final EmoteHomeEvent event, final GameQueue gameQueue, final User reactor) {
        try {
            GameQueueEditor gameQueueEditor = event.getGameQueueEditor();
            String emoteName = event.getEmote().getName();
            if (emoteName.equals(DAGGER_EMOTE)) {
                GameQueueAction action = gameQueueEditor.addUser(gameQueue.getId(), reactor.getHomedUser());
                updateMessage(event, gameQueue, event.getMessage(), action, false);
            } else if (emoteName.equals(ROTATE_EMOTE)) {
                GameQueueAction action = gameQueueEditor.rotateUser(gameQueue.getId(), reactor.getHomedUser());
                updateMessage(event, gameQueue, event.getMessage(), action, false);
            } else if (emoteName.equals(READY_EMOTE)) {
                GameQueueAction action = gameQueueEditor.readyUser(gameQueue.getId(), reactor.getHomedUser());
                updateMessage(event, gameQueue, event.getMessage(), action, false);
            } else if (emoteName.equals(LG_EMOTE)) {
                GameQueueAction action = gameQueueEditor.lgUser(gameQueue.getId(), reactor.getHomedUser());
                updateMessage(event, gameQueue, event.getMessage(), action, false);
            } else if (emoteName.equals(ON_CALL_EMOTE)) {
                GameQueueAction action = gameQueueEditor.onCallUser(gameQueue.getId(), reactor.getHomedUser());
                updateMessage(event, gameQueue, event.getMessage(), action, false);
            } else if (emoteName.equals(LEAVE_EMOTE)) {
                GameQueueAction action = gameQueueEditor.dequeueUser(gameQueue.getId(), reactor.getHomedUser());
                updateMessage(event, gameQueue, event.getMessage(), action, false);
            }
        } catch (UserError | BotHomeError e) {
        } finally {
            event.getMessage().removeEmote(event.getEmote(), reactor);
        }
    }

    public static void removeEmote(final EmoteHomeEvent event, final GameQueue gameQueue, final User reactor) {}

    public static void updateMessage(final MessageHomeEvent event, final GameQueue gameQueue, final Message message,
            final GameQueueAction action, final boolean verbose) throws BotHomeError {
        String text = createMessage(event.getGameQueueEditor(), gameQueue, event.getHomeEditor().getTimeZone());
        message.updateText(text);
        respondToAction(event, gameQueue, action, verbose);
    }

    public static void respondToAction(final MessageHomeEvent event, final GameQueue gameQueue,
            final GameQueueAction action, final boolean verbose) throws BotHomeError {
        String response = "";

        if (action.hasEvent(GameQueueAction.Event.GAME_STARTED) && verbose) {
            response = combine(response, "The game is about to start!");
        }

        if (action.hasEvent(GameQueueAction.Event.GAME_OPENED) && verbose) {
            response = combine(response, "The game queue is now open!");
        }

        if (action.hasEvent(GameQueueAction.Event.MIN_PLAYERS) && verbose) {
            response = combine(response, GameQueueUtils.getMinimumPlayersSetMessage(action.getMiniumumPlayers()));
        }

        if (action.hasEvent(GameQueueAction.Event.MAX_PLAYERS) && verbose) {
            response = combine(response, GameQueueUtils.getMaximumPlayersSetMessage(action.getMaximumPlayers()));
        }

        if (!action.getQueuedPlayers().isEmpty() && verbose) {
            response = combine(response, GameQueueUtils.getPlayersQueuedMessage(action.getQueuedPlayers()));
        }

        if (!action.getDequeuedPlayers().isEmpty() && verbose) {
            response = combine(response, GameQueueUtils.getPlayersDequeuedMessage(action.getDequeuedPlayers()));
        }

        if (!action.getReadiedPlayers().isEmpty() && verbose) {
            response = combine(response, GameQueueUtils.getPlayersReadyMessage(action.getReadiedPlayers()));
        }

        if (!action.getUnreadiedPlayers().isEmpty() && verbose) {
            response = combine(response, GameQueueUtils.getPlayersUnreadyMessage(action.getUnreadiedPlayers()));
        }

        if (!action.getLgedPlayers().isEmpty() && verbose) {
            response = combine(response, GameQueueUtils.getPlayersLgedMessage(action.getLgedPlayers()));
        }

        if (!action.getPermanentPlayers().isEmpty() && verbose) {
            response = combine(response, GameQueueUtils.getPlayersPermanentMessage(action.getPermanentPlayers()));
        }

        if (!action.getOnCallPlayers().isEmpty() && verbose) {
            response = combine(response, GameQueueUtils.getPlayersOnCallMessage(action.getOnCallPlayers()));
        }

        if (!action.getRsvpedPlayers().isEmpty() && verbose) {
            response = combine(response, GameQueueUtils.getPlayersRsvpedMessage(action.getRsvpedPlayers()));
        }

        if (!action.getEnteredGamePlayers().isEmpty()) {
            response = combine(response, GameQueueUtils.getPlayersEnterGameMessage(action.getEnteredGamePlayers()));
        }

        if (!action.getNoShowedPlayers().isEmpty()) {
            response = combine(response, GameQueueUtils.getPlayersNoShowedMessage(action.getNoShowedPlayers()));
        }

        if (!action.getOnDeckedPlayers().isEmpty()) {
            response = combine(response, GameQueueUtils.getPlayersOnDeckedMessage(action.getOnDeckedPlayers()));
        }

        if (!action.getRsvpExpiredPlayers().isEmpty()) {
            response = combine(response,
                    GameQueueUtils.getPlayersReservationExpiredMessage(action.getRsvpExpiredPlayers()));
        }

        if (!action.getGamerTagMap().isEmpty() && verbose) {
            response = combine(response, GameQueueUtils.getGamerTagsChangedMessage(action.getGamerTagMap()));
        }

        if (action.hasEvent(GameQueueAction.Event.GAMER_TAG_VARIABLE) && verbose) {
            response =
                    combine(response, GameQueueUtils.getGamerTagVariableChangedMessage(action.getGamerTagVariable()));
        }

        response = combine(response, gameQueue.getGame().getGameBehavior().respondToAction(action, verbose));

        if (action.hasEvent(GameQueueAction.Event.START_TIME) && verbose) {
            response = combine(response, GameQueueUtils.getStartTimeScheduledMessage(action.getStartTime(),
                    event.getHomeEditor().getTimeZone()));
        }

        if (!Strings.isBlank(response)) {
            event.say(response);
        }
    }

    public static String createMessage(final GameQueueEditor gameQueueEditor, final GameQueue gameQueue,
            final String timeZone) {
        StringBuilder text = new StringBuilder();
        Game game = gameQueue.getGame();

        if (gameQueue.isClosed()) {
            text.append("The **").append(game.getName()).append("** Game Queue has been closed.\n");
            return text.toString();
        }
        text.append("**").append(game.getName()).append("** Game Queue\n");

        if (gameQueue.getStartTime() != null) {
            ZonedDateTime time = ZonedDateTime.ofInstant(gameQueue.getStartTime(), ZoneId.of(timeZone));
            text.append(" scheduled to start at ⏰ ").append(Time.toReadableString(time)).append("\n");
        }

        game.getGameBehavior().appendMessageHeader(text, gameQueue);
        text.append("\n");

        appendPlayerList(text, gameQueue.getGamePlayers(), "CSS", "In the Game", "No active game.", null,
            (player, t) -> {
                if (!Strings.isBlank(gameQueue.getGamerTagVariable())) {
                    String gamerTag = gameQueueEditor.getGamerTag(player, gameQueue.getGamerTagVariable());
                    if (!Strings.isBlank(gamerTag)) {
                        t.append(" [").append(gamerTag).append(']');
                    }
                }
                if (gameQueue.isLg(player)) {
                    t.append(" (LG " + LG_EMOTE + ")");
                } else if (gameQueue.isPermanent(player)) {
                    t.append(" (" + STREAMING_EMOTE + ")");
                }
            });

        List<HomedUser> onDeckPlayers = gameQueue.getOnDeckPlayers();
        if (!onDeckPlayers.isEmpty()) {
            boolean hasNoShows = onDeckPlayers.stream().anyMatch(gameQueue::isNoShow);
            appendPlayerList(text, onDeckPlayers, "Diff", "On Deck", null,
                (player, t) -> {
                    if (gameQueue.isReady(player)) {
                        t.append("+ ");
                    } else if (gameQueue.isNoShow(player)) {
                        t.append("- ");
                    } else {
                        if (hasNoShows) {
                            t.append("~ ");
                        } else {
                            t.append("- ");
                        }
                    }
            } , (player, t) -> {
                    if (gameQueue.isReady(player)) {
                        t.append(" (ready)");
                    } else if (gameQueue.isNoShow(player)) {
                        t.append(" (no show)");
                    } else {
                        t.append(" (not ready)");
                    }
            });
        }

        List<HomedUser> waitQueue = gameQueue.getWaitQueue();
        if (!waitQueue.isEmpty() || onDeckPlayers.isEmpty()) {
            appendPlayerList(text, gameQueue.getWaitQueue(), "FIX", "Queue", "No one is waiting.", null,
            (player, t) -> {
                if (gameQueue.isOnCall(player)) {
                    t.append(" (if needed " + ON_CALL_EMOTE + ")");
                }
            });
        }

        List<HomedUser> rsvpList = gameQueue.getRsvpList();

        if (!rsvpList.isEmpty()) {
            appendPlayerList(text, gameQueue.getRsvpList(), "HTTP", "Reservations", null, null,
                (player, t) -> {
                    try {
                        Instant rsvpTime = gameQueue.getRsvpTime(player);
                        if (Instant.now().compareTo(rsvpTime) < 0) {
                            ZonedDateTime time = ZonedDateTime.ofInstant(rsvpTime, ZoneId.of(timeZone));
                            t.append(" (").append(Time.toReadableString(time)).append(')');
                        } else {
                            t.append(" (late)");
                        }
                    } catch (UserError e) {
                    }
                });
        }

        game.getGameBehavior().appendMessageFooter(text, gameQueue);

        text.append("React with:\n");
        text.append(DAGGER_EMOTE + ": To join the queue\t\t" + ON_CALL_EMOTE + ": To join queue only if needed\t\t"
                        + ROTATE_EMOTE + ": To rotate (leave and rejoin queue)\n");
        text.append(LG_EMOTE + ": When it's your LG\t\t" + READY_EMOTE + ": To signal ready when on deck\t\t"
                + LEAVE_EMOTE + ": To leave the game and queue\n");
        return text.toString();
    }

    public static String getProximityServerMessage(final String proximityServer) {
        if (proximityServer == null)  {
            return "There is not a proximity voice server.";
        }

        return String.format("The proximity voice server is `%s`", proximityServer);
    }

    public static String getMinimumPlayersSetMessage(final int minPlayers) {
        return String.format("The minimum number of players has been set to %d.", minPlayers);
    }

    public static String getMaximumPlayersSetMessage(final int maxPlayers) {
        return String.format("The maximum number of players has been set to %d.", maxPlayers);
    }

    public static String getStartTimeScheduledMessage(final Instant startTime, final String timeZone) {
        ZonedDateTime time = ZonedDateTime.ofInstant(startTime, ZoneId.of(timeZone));
        return String.format("A game is scheduled to start at %s", Time.toReadableString(time));
    }

    public static String getPlayersQueuedMessage(final List<HomedUser> players) {
        return getPlayersMessage("Added", "to the queue.", players);
    }

    public static String getPlayersDequeuedMessage(final List<HomedUser> players) {
        return getPlayersMessage("Removed", "from the queue.", players);
    }

    public static String getPlayersLgedMessage(final List<HomedUser> players) {
        return getPlayersMessage("Marked", "as LG.", players);
    }

    public static String getPlayersOnCallMessage(final List<HomedUser> players) {
        return getPlayersMessage("Marked", "as if needed.", players);
    }

    public static String getPlayersPermanentMessage(final List<HomedUser> players) {
        return getPlayersMessage("Marked", "as permanent.", players);
    }

    public static String getPlayersReadyMessage(final List<HomedUser> players) {
        return getPlayersMessage(null, (players.size() > 1 ? "are": "is") + " ready to play.", players);
    }

    public static String getPlayersUnreadyMessage(final List<HomedUser> players) {
        return getPlayersMessage(null, (players.size() > 1 ? "were": "has been") + " put back into the queue.", players);
    }

    public static String getPlayersRsvpedMessage(final List<HomedUser> players) {
        return getPlayersMessage(null, (players.size() > 1 ? "have": "has") + " made a reservation.", players);
    }

    public static String getPlayersNoShowedMessage(final List<HomedUser> players) {
        StringBuilder text = new StringBuilder();
        appendPlayerList(text, players, true);
        text.append(" did not show up and have been placed in time out!");
        return text.toString();
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

    public static String getPlayersEnterGameMessage(final List<HomedUser> players) {
        StringBuilder text = new StringBuilder();
        appendPlayerList(text, players, false);
        if (players.size() > 1) {
            text.append(" have");
        } else {
            text.append(" has");
        }
        text.append(" entered the game.");
        return text.toString();
    }

    public static String getPlayersReservationExpiredMessage(final List<HomedUser> players) {
        StringBuilder text = new StringBuilder();
        appendPlayerList(text, players, true);
        if (players.size() > 1) {
            text.append(" have");
        } else {
            text.append(" has");
        }
        text.append(" a reservation and should react to the queue with " + DAGGER_EMOTE + " when they are ready to play!");
        return text.toString();
    }

    private static String getGamerTagsChangedMessage(final Map<HomedUser, String> gamerTagMap) {
        StringBuilder text = new StringBuilder();
        boolean first = true;
        for (Map.Entry<HomedUser, String> entry : gamerTagMap.entrySet()) {
            if (!first) {
                text.append(" ");
            }
            text.append(entry.getKey().getName()).append("'s gamer tag has been updated to ");
            text.append(entry.getValue()).append(".");
            first = false;
        }
        return text.toString();
    }

    private static String getGamerTagVariableChangedMessage(final String gamerTagVariable) {
        return String.format("The new gamer tag variable is %s.", gamerTagVariable);
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

    private interface AdditionalPlayerInfo {
        void appendInfo(HomedUser player, StringBuilder text);
    }

    private static void appendPlayerList(final StringBuilder text, final List<HomedUser> players, final String syntax,
            final String title, final String emptyMessage, final AdditionalPlayerInfo prefixInfo,
            final AdditionalPlayerInfo postfixInfo) {
        if (players.isEmpty()) {
            text.append(emptyMessage).append("\n\n");
        } else {
            text.append(title).append(" ```").append(syntax).append('\n');
            int count = 1;
            for (HomedUser player : players) {
                if (prefixInfo != null) {
                    prefixInfo.appendInfo(player, text);
                } else {
                    text.append(count++).append(") ");
                }
                text.append(player.getName());
                if (postfixInfo != null) {
                    postfixInfo.appendInfo(player, text);
                }
                text.append('\n');
            }
            text.append("```\n");
        }
    }

    public static String combine(final String a, final String b) {
        if (Strings.isBlank(a)) {
            return b;
        }
        return a + " " + b;
    }
}
