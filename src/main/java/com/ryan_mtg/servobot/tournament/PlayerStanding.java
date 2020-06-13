package com.ryan_mtg.servobot.tournament;

import com.ryan_mtg.servobot.channelfireball.mfo.model.Player;
import com.ryan_mtg.servobot.channelfireball.mfo.model.Record;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class PlayerStanding implements Comparable<PlayerStanding> {
    private Player player;
    private Record record;

    @Override
    public int compareTo(final PlayerStanding playerStanding) {
        return -record.compareTo(playerStanding.record);
    }
}
