package com.ryan_mtg.servobot.tournament;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class PlayerStanding implements Comparable<PlayerStanding> {
    private final Player player;
    private final int rank;
    private final boolean important;
    private final boolean leader;
    private final Record record;
    private final Result result;
    private final DecklistDescription decklist;
    private final boolean hasDropped;
    private final Player opponent;
    private final DecklistDescription opponentsDecklist;
    private final boolean bounty;

    public enum Result {
        NONE,
        WIN,
        LOSS,
        DRAW;

        public static Result reverse(final Result result) {
            switch (result) {
                case NONE:
                    return NONE;
                case WIN:
                    return LOSS;
                case LOSS:
                    return WIN;
                case DRAW:
                    return DRAW;
                default:
                    throw new IllegalStateException("Unknown result: " + result);
            }
        }
    }

    @Override
    public int compareTo(final PlayerStanding playerStanding) {
        int recordCompare = -record.compareTo(playerStanding.record);
        if (recordCompare != 0) {
            return recordCompare;
        }
        return rank - playerStanding.getRank();
    }

    public boolean won() {
        return result == Result.WIN;
    }


    public boolean loss() {
        return result == Result.LOSS;
    }

    public boolean drew() {
        return result == Result.DRAW;
    }
}