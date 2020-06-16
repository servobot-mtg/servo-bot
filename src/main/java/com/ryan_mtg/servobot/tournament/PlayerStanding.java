package com.ryan_mtg.servobot.tournament;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class PlayerStanding implements Comparable<PlayerStanding> {
    private Player player;
    private int rank;
    private Player opponent;
    private Record record;
    private DecklistDescription decklist;
    private DecklistDescription opponentsDecklist;

    @Override
    public int compareTo(final PlayerStanding playerStanding) {
        int recordCompare = -record.compareTo(playerStanding.record);
        if (recordCompare != 0) {
            return recordCompare;
        }
        return rank - playerStanding.getRank();
    }
}
