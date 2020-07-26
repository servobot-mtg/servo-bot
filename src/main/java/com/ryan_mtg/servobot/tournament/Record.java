package com.ryan_mtg.servobot.tournament;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter @EqualsAndHashCode
public class Record implements Comparable<Record> {
    private int wins;
    private int losses;
    private int draws;
    private boolean dropped;

    private Record(final int wins, final int losses, final int draws, final boolean dropped) {
        this.wins = wins;
        this.losses = losses;
        this.draws = draws;
        this.dropped = dropped;
    }

    public int getPoints() {
        return 3 * wins + draws;
    }

    public static Record newRecord(final int wins, final int losses, final int draws, final boolean dropped) {
        return new Record(wins, losses, draws, dropped);
    }

    public static Record newRecord(final int points, final int roundsPlayed, final boolean dropped) {
        int wins = points / 3;
        int draws = points % 3;
        int losses = roundsPlayed - wins - draws;
        return new Record(wins, losses, draws, dropped);
    }

    public static Record newRecord(final int points, final int roundsPlayed) {
        return newRecord(points, roundsPlayed, false);
    }

    @Override
    public int compareTo(final Record other) {
        if (getPoints() != other.getPoints()) {
            return getPoints() - other.getPoints();
        }

        if (getWins() != other.getWins()) {
            return getWins() - other.getWins();
        }

        return other.getLosses() - getLosses();
    }

    @Override
    public String toString() {
        if (draws == 0) {
            return String.format("%d-%d", wins, losses);
        }
        return String.format("%d-%d-%d", wins, losses, draws);
    }

    public Record addWin() {
        return new Record(wins + 1, losses, draws, dropped);
    }

    public Record addLoss() {
        return new Record(wins, losses + 1, draws, dropped);
    }

    public Record addDraw() {
        return new Record(wins, losses, draws + 1, dropped);
    }
}