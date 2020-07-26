package com.ryan_mtg.servobot.tournament;

import com.ryan_mtg.servobot.utility.Time;
import lombok.Getter;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

public class Pairings {
    @Getter
    private PlayerSet playerSet;

    @Getter
    private int round;

    @Getter
    private Instant roundStartTime;

    private Map<Player, Player> opponentMap = new HashMap<>();
    private Map<Player, PlayerStanding.Result> resultMap = new HashMap<>();
    private boolean done = true;

    public boolean isDone() {
        return done;
    }

    public Player getOpponent(final Player player) {
        return opponentMap.get(player);
    }

    public boolean hasDropped(final Player player) {
        return !opponentMap.containsKey(player);
    }

    public boolean hasResult(final Player player) {
        return resultMap.containsKey(player) && resultMap.get(player) != PlayerStanding.Result.NONE;
    }

    public PlayerStanding.Result getResult(final Player player) {
        return resultMap.get(player);
    }

    public void add(final Player player, final Player opponent, final PlayerStanding.Result result) {
        add(player, opponent);
        if (result == PlayerStanding.Result.NONE) {
            done = false;
        }
        resultMap.put(player, result);
    }

    public void add(final Player player, final Player opponent) {
        Player existingPlayer = playerSet.merge(player);
        Player existingOpponent = playerSet.merge(opponent);
        opponentMap.put(existingPlayer, existingOpponent);
        done = false;
    }

    public Pairings(final PlayerSet playerSet, final int round, final Instant roundStartTime) {
        this.playerSet = playerSet;
        this.round = round;
        this.roundStartTime = roundStartTime;
    }

    public Instant getRoundEndTime() {
        return roundStartTime.plus(70, ChronoUnit.MINUTES);
    }

    public String getTimeSinceStartOfRound() {
        if (roundStartTime == null) {
            return "No clue!";
        }
        return Time.toReadableString(Duration.between(roundStartTime, Instant.now()));
    }

    public String getTimeUntilEndOfRound() {
        if (roundStartTime == null) {
            return "No clue!";
        }

        Instant now = Instant.now();
        if (now.compareTo(getRoundEndTime()) < 0) {
            return String.format("About %s", Time.toReadableString(Duration.between(Instant.now(), getRoundEndTime())));
        }
        return "Any time now";
    }
}
