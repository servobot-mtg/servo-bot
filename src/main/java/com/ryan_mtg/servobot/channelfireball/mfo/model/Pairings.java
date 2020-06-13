package com.ryan_mtg.servobot.channelfireball.mfo.model;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class Pairings {
    @Getter
    private PlayerSet playerSet;

    @Getter
    private int round;

    private Map<Player, Player> opponentMap = new HashMap<>();

    public Player getOpponent(final Player player) {
        return opponentMap.get(player);
    }

    public void add(final Player player, final Player opponent) {
        Player existingPlayer = playerSet.findByArenaName(player.getArenaName());
        Player existingOpponent = playerSet.findByArenaName(opponent.getArenaName());
        opponentMap.put(existingPlayer, existingOpponent);
    }

    public Pairings(final PlayerSet playerSet, final int round) {
        this.playerSet = playerSet;
        this.round = round;
    }
}
