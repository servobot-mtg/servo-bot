package com.ryan_mtg.servobot.model.game_queue;

import com.ryan_mtg.servobot.user.HomedUser;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Builder
public class GameQueueAction {
    public enum Event {
        CODE,
        SERVER,
        PROXIMITY_SERVER,
        GAMER_TAG,
        GAMER_TAG_VARIABLE,
        VERSION,
        START_TIME,
        GAME_OPENED,
        GAME_STARTED,
        MIN_PLAYERS,
        MAX_PLAYERS,
    }

    public enum PlayerAction {
        QUEUED,
        DEQUEUED,
        ENTERED_GAME,
        ON_DECKED,
        READIED,
        UNREADIED,
        NO_SHOWED,
        LGED,
        MOVED,
        RSVPED,
        RSVP_EXPIRED,
        MADE_PERMANENT,
        ON_CALLED,
        NOTE_ADDED,
    }

    @Getter @Builder.Default
    private final Map<HomedUser, String> gamerTagMap = new HashMap<>();

    @Builder.Default
    private final Map<Event, Object> eventMap = new HashMap<>();

    @Builder.Default
    private final Map<PlayerAction, List<HomedUser>> playerActionMap = new HashMap<>();

    public boolean hasEvent(final Event event) {
        return eventMap.containsKey(event);
    }

    public boolean hasPlayers(final PlayerAction action) {
        return playerActionMap.containsKey(action) && !playerActionMap.get(action).isEmpty();
    }

    public List<HomedUser> getPlayers(final PlayerAction action) {
        if (playerActionMap.containsKey(action)) {
            return playerActionMap.get(action);
        }
        return Collections.emptyList();
    }

    public String getGamerTagVariable() {
        return (String) eventMap.get(Event.GAMER_TAG_VARIABLE);
    }

    public int getMiniumumPlayers() {
        return (Integer) eventMap.get(Event.MIN_PLAYERS);
    }

    public int getMaximumPlayers() {
        return (Integer) eventMap.get(Event.MAX_PLAYERS);
    }

    public Instant getStartTime() {
        return (Instant) eventMap.get(Event.START_TIME);
    }

    public String getCode() {
        return (String) eventMap.get(Event.CODE);
    }

    public String getServer() {
        return (String) eventMap.get(Event.SERVER);
    }

    public String getProximityServer() {
        return (String) eventMap.get(Event.PROXIMITY_SERVER);
    }

    public GameQueue.Version getVersion() {
        return (GameQueue.Version) eventMap.get(Event.VERSION);
    }

    public void merge(final GameQueueAction action) {
        eventMap.putAll(action.eventMap);
        action.playerActionMap.forEach((playerAction, list)
                -> playerActionMap.computeIfAbsent(playerAction, pa -> new ArrayList<>()).addAll(list));

        gamerTagMap.putAll(action.gamerTagMap);
    }

    public void playerEntered(final HomedUser player) {
        playerActionMap.computeIfAbsent(PlayerAction.ENTERED_GAME, pa -> new ArrayList<>()).add(player);
        if (playerActionMap.containsKey(PlayerAction.ON_DECKED)) {
            playerActionMap.get(PlayerAction.ON_DECKED).remove(player);
        }
    }

    public static GameQueueAction emptyAction() {
        return GameQueueAction.builder().build();
    }

    public static GameQueueAction gameOpened() {
        return GameQueueAction.builder().eventMap(createMap(Event.GAME_OPENED, null)).build();
    }

    public static GameQueueAction gameStarted() {
        return GameQueueAction.builder().eventMap(createMap(Event.GAME_STARTED, null)).build();
    }

    public static GameQueueAction codeChanged(final String code, final String server, final GameQueue.Version version) {
        Map<Event, Object> eventMap = createMap(Event.CODE, code);
        eventMap.put(Event.SERVER, server);
        eventMap.put(Event.VERSION, version);
        return GameQueueAction.builder().eventMap(eventMap).build();
    }

    public static GameQueueAction proximityServerChanged(final String proximityServer) {
        return GameQueueAction.builder().eventMap(createMap(Event.PROXIMITY_SERVER, proximityServer)).build();
    }

    public static GameQueueAction startTimeChanged(final Instant startTime) {
        return GameQueueAction.builder().eventMap(createMap(Event.START_TIME, startTime)).build();
    }

    public static GameQueueAction setMinimumPlayers(final int minPlayers) {
        return GameQueueAction.builder().eventMap(createMap(Event.MIN_PLAYERS, minPlayers)).build();
    }

    public static GameQueueAction setMaximumPlayers(final int maxPlayers) {
        return GameQueueAction.builder().eventMap(createMap(Event.MAX_PLAYERS, maxPlayers)).build();
    }

    public static GameQueueAction playerQueued(final HomedUser player) {
        return GameQueueAction.builder().playerActionMap(createMap(player, PlayerAction.QUEUED)).build();
    }

    public static GameQueueAction playerDequeued(final HomedUser player) {
        return GameQueueAction.builder().playerActionMap(createMap(player, PlayerAction.DEQUEUED)).build();
    }

    public static GameQueueAction noteAdded(final HomedUser player) {
        return GameQueueAction.builder().playerActionMap(createMap(player, PlayerAction.NOTE_ADDED)).build();
    }

    public static GameQueueAction playerReadied(final HomedUser player) {
        return GameQueueAction.builder().playerActionMap(createMap(player, PlayerAction.READIED)).build();
    }

    public static GameQueueAction playerUnreadied(final HomedUser player) {
        return GameQueueAction.builder().playerActionMap(createMap(player, PlayerAction.UNREADIED)).build();
    }

    public static GameQueueAction playerNoShowed(HomedUser player) {
        return GameQueueAction.builder().playerActionMap(createMap(player, PlayerAction.NO_SHOWED)).build();
    }

    public static GameQueueAction playerEnteredGame(final HomedUser player) {
        return GameQueueAction.builder().playerActionMap(createMap(player, PlayerAction.ENTERED_GAME)).build();
    }

    public static GameQueueAction playerOnDecked(final HomedUser player) {
        return GameQueueAction.builder().playerActionMap(createMap(player, PlayerAction.ON_DECKED)).build();
    }

    public static GameQueueAction playerLged(final HomedUser player) {
        return GameQueueAction.builder().playerActionMap(createMap(player, PlayerAction.LGED)).build();
    }

    public static GameQueueAction playerMoved(final HomedUser player) {
        return GameQueueAction.builder().playerActionMap(createMap(player, PlayerAction.MOVED)).build();
    }

    public static GameQueueAction playerRsvped(final HomedUser player) {
        return GameQueueAction.builder().playerActionMap(createMap(player, PlayerAction.RSVPED)).build();
    }

    public static GameQueueAction playerReservationExpired(final HomedUser player) {
        return GameQueueAction.builder().playerActionMap(createMap(player, PlayerAction.RSVP_EXPIRED)).build();
    }

    public static GameQueueAction playerMadePermanent(final HomedUser player) {
        return GameQueueAction.builder().playerActionMap(createMap(player, PlayerAction.MADE_PERMANENT)).build();
    }

    public static GameQueueAction playerOnCalled(final HomedUser player) {
        return GameQueueAction.builder().playerActionMap(createMap(player, PlayerAction.ON_CALLED)).build();
    }

    public static GameQueueAction gamerTagVariableChanged(final String gamerTagVariable) {
        return GameQueueAction.builder().eventMap(createMap(Event.GAMER_TAG_VARIABLE, gamerTagVariable)).build();
    }

    public static GameQueueAction gamerTagChanged(HomedUser user, String gamerTag) {
        return GameQueueAction.builder().gamerTagMap(Collections.singletonMap(user, gamerTag)).build();
    }

    private static List<HomedUser> merge(final List<HomedUser> a, final List<HomedUser> b) {
        if (a == null) {
            return b;
        }

        if (b == null) {
            return a;
        }

        return Stream.concat(a.stream(), b.stream()).collect(Collectors.toList());
    }

    private static Map<Event, Object> createMap(final Event action, final Object value) {
        Map<Event, Object> map = new HashMap<>();
        map.put(action, value);
        return map;
    }

    private static Map<PlayerAction, List<HomedUser>> createMap(final HomedUser player, final PlayerAction action) {
        Map<PlayerAction, List<HomedUser>> map = new HashMap<>();
        List<HomedUser> list = new ArrayList<>();
        list.add(player);
        map.put(action, list);
        return map;
    }
}