package com.ryan_mtg.servobot.channelfireball.mfo.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter @EqualsAndHashCode
public class Record implements Comparable<Record> {
    private int wins;
    private int losses;
    private int draws;

    private Record(final int wins, final int losses, final int draws) {
        this.wins = wins;
        this.losses = losses;
        this.draws = draws;
    }

    public int getPoints() {
        return 3 * wins + draws;
    }

    public static Record newRecord(final int points, final int roundsPlayed) {
        int wins = points / 3;
        int draws = points % 3;
        int losses = roundsPlayed - wins - draws;
        return new Record(wins, losses, draws);
    }

    @Override
    public int compareTo(@NotNull final Record other) {
        if (getPoints() != other.getPoints()) {
            return getPoints() - other.getPoints();
        }
        return getWins() - other.getWins();
    }

    @Override
    public String toString() {
        if (draws == 0) {
            return String.format("%d-%d", wins, losses);
        }
        return String.format("%d-%d-%d", wins, losses, draws);
    }
}
