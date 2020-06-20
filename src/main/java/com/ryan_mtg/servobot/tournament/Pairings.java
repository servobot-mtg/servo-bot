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

    public Player getOpponent(final Player player) {
        return opponentMap.get(player);
    }

    public void add(final Player player, final Player opponent) {
        Player existingPlayer = playerSet.findByArenaName(player.getArenaName());
        Player existingOpponent = playerSet.findByArenaName(opponent.getArenaName());
        opponentMap.put(existingPlayer, existingOpponent);
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
        return Time.toReadableString(Duration.between(roundStartTime, Instant.now()));
    }

    public String getTimeUntilEndOfRound() {
        Instant now = Instant.now();
        if (now.compareTo(getRoundEndTime()) < 0) {
            return String.format("About %s", Time.toReadableString(Duration.between(Instant.now(), getRoundEndTime())));
        }
        return "Any time now";
    }
}
