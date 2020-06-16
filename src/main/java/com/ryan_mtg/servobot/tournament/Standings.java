package com.ryan_mtg.servobot.tournament;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Standings {
    @Getter
    private PlayerSet playerSet;

    @Getter
    private int round;

    private Map<Player, Record> playerRecord = new HashMap<>();
    private Map<Player, Integer> playerRank = new HashMap<>();

    public Standings(final PlayerSet playerSet, final int round) {
        this.playerSet = playerSet;
        this.round = round;
    }

    public void add(final Player player, final Record record) {
        Player newPlayer = playerSet.add(player);
        playerRecord.put(newPlayer, record);
    }

    public void setRank(final Player player, final int rank) {
        playerRank.put(player, rank);
    }

    public List<RecordCount> getRecordCounts(final int maxLosses) {
        Map<Record, Integer> recordCountMap = new HashMap<>();
        playerRecord.values().stream().filter(record -> record.getLosses() <= maxLosses).forEach(record -> {
            int count = recordCountMap.computeIfAbsent(record, r -> 0);
            recordCountMap.put(record, count + 1);
        });

        List<RecordCount> recordCounts = new ArrayList<>();
        for (Map.Entry<Record, Integer> entry : recordCountMap.entrySet()) {
            recordCounts.add(new RecordCount(entry.getKey(), entry.getValue()));
        }
        Collections.sort(recordCounts, new RecordCountComparator());
        return recordCounts;
    }

    private static class RecordCountComparator implements Comparator<RecordCount> {
        @Override
        public int compare(final RecordCount recordCount, final RecordCount otherRecordCount) {
            return -recordCount.getRecord().compareTo(otherRecordCount.getRecord());
        }
    }

    public Record getRecord(final Player player) {
        return playerRecord.get(player);
    }

    public int getRank(final Player player) {
        return playerRank.get(player);
    }
}
