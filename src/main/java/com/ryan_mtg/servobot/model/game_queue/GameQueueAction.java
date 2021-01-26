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
        ON_BETA,
        START_TIME,
        GAME_STARTED,
        MIN_PLAYERS,
        MAX_PLAYERS,
    }

    /*
    @Getter
    private String code;

    @Getter
    private String server;

    @Getter
    private String proximityServer;

    @Getter
    private Boolean onBeta;

    @Getter
    private Instant startTime;

    @Getter
    private boolean gameStarted;
     */

    @Getter @Builder.Default
    private List<HomedUser> queuedPlayers = new ArrayList<>();

    @Getter @Builder.Default
    private List<HomedUser> dequeuedPlayers = new ArrayList<>();

    @Getter @Builder.Default
    private List<HomedUser> enteredGamePlayers = new ArrayList<>();

    @Getter @Builder.Default
    private List<HomedUser> onDeckedPlayers = new ArrayList<>();

    @Getter @Builder.Default
    private List<HomedUser> readiedPlayers = new ArrayList<>();

    @Getter @Builder.Default
    private List<HomedUser> unreadiedPlayers = new ArrayList<>();

    @Getter @Builder.Default
    private List<HomedUser> lgedPlayers = new ArrayList<>();

    @Getter @Builder.Default
    private List<HomedUser> movedPlayers = new ArrayList<>();

    @Getter @Builder.Default
    private List<HomedUser> rsvpedPlayers = new ArrayList<>();

    @Getter @Builder.Default
    private List<HomedUser> rsvpExpiredPlayers = new ArrayList<>();

    @Getter @Builder.Default
    private List<HomedUser> permanentPlayers = new ArrayList<>();

    @Getter @Builder.Default
    private List<HomedUser> onCallPlayers = new ArrayList<>();

    @Getter @Builder.Default
    private Map<HomedUser, String> gamerTagMap = new HashMap<>();

    @Builder.Default
    private Map<Event, Object> eventMap = new HashMap<>();

    public boolean hasEvent(final Event event) {
        return eventMap.containsKey(event);
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

    public Boolean getOnBeta() {
        return (Boolean) eventMap.get(Event.ON_BETA);
    }

    public void merge(final GameQueueAction action) {
        eventMap.putAll(action.eventMap);
        queuedPlayers = merge(queuedPlayers, action.queuedPlayers);
        dequeuedPlayers = merge(dequeuedPlayers, action.dequeuedPlayers);
        onDeckedPlayers = merge(onDeckedPlayers, action.onDeckedPlayers);
        enteredGamePlayers = merge(enteredGamePlayers, action.enteredGamePlayers);
        readiedPlayers = merge(readiedPlayers, action.readiedPlayers);
        unreadiedPlayers = merge(unreadiedPlayers, action.unreadiedPlayers);
        lgedPlayers = merge(lgedPlayers, action.lgedPlayers);
        permanentPlayers = merge(permanentPlayers, action.permanentPlayers);
        onCallPlayers = merge(onCallPlayers, action.onCallPlayers);
        movedPlayers = merge(movedPlayers, action.movedPlayers);
        rsvpedPlayers = merge(rsvpedPlayers, action.rsvpedPlayers);
        rsvpExpiredPlayers = merge(rsvpExpiredPlayers, action.rsvpExpiredPlayers);
        gamerTagMap.putAll(action.gamerTagMap);
    }

    public static GameQueueAction emptyAction() {
        return GameQueueAction.builder().build();
    }

    public static GameQueueAction gameStarted() {
        return GameQueueAction.builder().eventMap(createMap(Event.GAME_STARTED, null)).build();
    }

    public static GameQueueAction codeChanged(final String code, final String server, final boolean isOnBeta) {
        Map<Event, Object> eventMap = createMap(Event.CODE, code);
        eventMap.put(Event.SERVER, server);
        eventMap.put(Event.ON_BETA, isOnBeta);
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
        return GameQueueAction.builder().queuedPlayers(Collections.singletonList(player)).build();
    }

    public static GameQueueAction playerDequeued(final HomedUser player) {
        return GameQueueAction.builder().dequeuedPlayers(Collections.singletonList(player)).build();
    }

    public static GameQueueAction playerReadied(final HomedUser player) {
        return GameQueueAction.builder().readiedPlayers(Collections.singletonList(player)).build();
    }

    public static GameQueueAction playerUnreadied(final HomedUser player) {
        return GameQueueAction.builder().unreadiedPlayers(Collections.singletonList(player)).build();
    }

    public static GameQueueAction playerEnteredGame(final HomedUser player) {
        return GameQueueAction.builder().enteredGamePlayers(Collections.singletonList(player)).build();
    }

    public static GameQueueAction playerOnDecked(final HomedUser player) {
        return GameQueueAction.builder().onDeckedPlayers(Collections.singletonList(player)).build();
    }

    public static GameQueueAction playerLged(final HomedUser player) {
        return GameQueueAction.builder().lgedPlayers(Collections.singletonList(player)).build();
    }

    public static GameQueueAction playerMoved(final HomedUser player) {
        return GameQueueAction.builder().movedPlayers(Collections.singletonList(player)).build();
    }

    public static GameQueueAction playerRsvped(final HomedUser player) {
        return GameQueueAction.builder().rsvpedPlayers(Collections.singletonList(player)).build();
    }

    public static GameQueueAction playerReservationExpired(final HomedUser player) {
        return GameQueueAction.builder().rsvpExpiredPlayers(Collections.singletonList(player)).build();
    }

    public static GameQueueAction playerPermanented(final HomedUser player) {
        return GameQueueAction.builder().permanentPlayers(Collections.singletonList(player)).build();
    }

    public static GameQueueAction playerOnCalled(final HomedUser player) {
        return GameQueueAction.builder().onCallPlayers(Collections.singletonList(player)).build();
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
}
