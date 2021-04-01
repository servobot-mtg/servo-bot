package com.ryan_mtg.servobot.mpl;

import lombok.Data;

@Data
public class Match implements Comparable<Match> {
    int round;
    Player player1;
    Player player2;

    public Match(final int round) {
        this.round = round;
    }

    @Override
    public int compareTo(final Match o) {
        return round - o.round;
    }
}