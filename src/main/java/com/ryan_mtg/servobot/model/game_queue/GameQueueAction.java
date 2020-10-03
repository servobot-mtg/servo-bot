package com.ryan_mtg.servobot.model.game_queue;

import com.ryan_mtg.servobot.user.HomedUser;
import lombok.Builder;
import lombok.Getter;

import java.util.Collections;
import java.util.List;

@Builder
public class GameQueueAction {
    public enum Event {
        CODE_CHANGED,
        PLAYERS_QUEUE,
        PLAYERS_LEAVE,
    }

    @Getter
    private Event event;

    @Getter
    private String code;

    @Getter
    private String server;

    @Getter
    private List<HomedUser> queuedPlayers;

    @Getter
    private List<HomedUser> dequeuedPlayers;

    public static GameQueueAction codeChanged(final String code, final String server) {
        return GameQueueAction.builder().event(Event.CODE_CHANGED).code(code).server(server).build();
    }

    public static GameQueueAction playersQueued(final List<HomedUser> players) {
        return GameQueueAction.builder().event(Event.PLAYERS_QUEUE).queuedPlayers(players).build();
    }

    public static GameQueueAction playerQueued(final HomedUser player) {
        return GameQueueAction.builder().event(Event.PLAYERS_QUEUE)
                .queuedPlayers(Collections.singletonList(player)).build();
    }

    public static GameQueueAction playersDequeued(final List<HomedUser> players) {
        return GameQueueAction.builder().event(Event.PLAYERS_LEAVE).dequeuedPlayers(players).build();
    }

    public static GameQueueAction playerDequeued(final HomedUser player) {
        return GameQueueAction.builder().event(Event.PLAYERS_LEAVE)
                .dequeuedPlayers(Collections.singletonList(player)).build();
    }
}
