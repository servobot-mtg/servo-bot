package com.ryan_mtg.servobot.channelfireball.mfo.model;

import java.util.HashMap;
import java.util.Map;

public class Standings {
    private PlayerSet playerSet;
    private int round;
    private Map<Player, Record> playerRecord = new HashMap<>();

    public Standings(final PlayerSet playerSet, final int round) {
        this.playerSet = playerSet;
        this.round = round;
    }

    public void add(final Player player, final Record record) {
        Player newPlayer = playerSet.add(player);
        playerRecord.put(newPlayer, record);
    }

    public Map<Record, Integer> getRecordCounts(final int maxLosses) {
        Map<Record, Integer> recordCountMap = new HashMap<>();
        playerRecord.values().stream().filter(record -> record.getLosses() <= maxLosses).forEach(record -> {
            int count = recordCountMap.computeIfAbsent(record, r -> 0);
            recordCountMap.put(record, count + 1);
        });
        return recordCountMap;
    }
}
