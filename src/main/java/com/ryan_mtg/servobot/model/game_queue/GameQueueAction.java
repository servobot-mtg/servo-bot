package com.ryan_mtg.servobot.model.game_queue;

import com.ryan_mtg.servobot.user.HomedUser;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Builder
public class GameQueueAction {
    @Getter
    private String code;

    @Getter
    private String server;

    @Getter
    private Boolean onBeta;

    @Getter
    private Instant startTime;

    @Getter
    private boolean gameStarted;

    @Getter @Builder.Default
    private List<HomedUser> queuedPlayers = new ArrayList<>();

    @Getter @Builder.Default
    private List<HomedUser> dequeuedPlayers = new ArrayList<>();

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

    public void merge(final GameQueueAction action) {
        if (action.code != null) {
            code = action.code;
        }
        if (action.server != null) {
            server = action.server;
        }
        if (action.onBeta != null) {
            onBeta = action.onBeta;
        }
        if (action.startTime != null) {
            startTime = action.startTime;
        }
        if (action.gameStarted) {
            gameStarted = true;
        }
        queuedPlayers = merge(queuedPlayers, action.queuedPlayers);
        dequeuedPlayers = merge(dequeuedPlayers, action.dequeuedPlayers);
        onDeckedPlayers = merge(onDeckedPlayers, action.onDeckedPlayers);
        readiedPlayers = merge(readiedPlayers, action.readiedPlayers);
        unreadiedPlayers = merge(unreadiedPlayers, action.unreadiedPlayers);
        lgedPlayers = merge(lgedPlayers, action.lgedPlayers);
        permanentPlayers = merge(permanentPlayers, action.permanentPlayers);
        onCallPlayers = merge(onCallPlayers, action.onCallPlayers);
        movedPlayers = merge(movedPlayers, action.movedPlayers);
        rsvpedPlayers = merge(rsvpedPlayers, action.rsvpedPlayers);
        rsvpExpiredPlayers = merge(rsvpExpiredPlayers, action.rsvpExpiredPlayers);
    }

    public static GameQueueAction emptyAction() {
        return GameQueueAction.builder().build();
    }

    public static GameQueueAction gameStarted() {
        return GameQueueAction.builder().gameStarted(true).build();
    }

    public static GameQueueAction codeChanged(final String code, final String server, final boolean isOnBeta) {
        return GameQueueAction.builder().code(code).server(server).onBeta(isOnBeta).build();
    }

    public static GameQueueAction startTimeChanged(final Instant startTime) {
        return GameQueueAction.builder().startTime(startTime).build();
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

    private static List<HomedUser> merge(final List<HomedUser> a, final List<HomedUser> b) {
        if (a == null) {
            return b;
        }

        if (b == null) {
            return a;
        }

        return Stream.concat(a.stream(), b.stream()).collect(Collectors.toList());
    }
}
