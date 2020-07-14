package com.ryan_mtg.servobot.tournament;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data @AllArgsConstructor
public class RecordCount {
    private Record record;
    private int count;

    public static String print(final List<RecordCount> recordCounts) {
        StringBuilder stringBuilder = new StringBuilder();
        boolean first = true;
        for (RecordCount recordCount : recordCounts) {
            if (!first) {
                stringBuilder.append(", ");
            }
            Record record = recordCount.getRecord();
            int players = recordCount.getCount();
            stringBuilder.append(record).append(": ").append(players).append(players == 1 ? " player" : " players");
            first = false;
        }
        return stringBuilder.toString();
    }
}
