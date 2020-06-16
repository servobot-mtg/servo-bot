package com.ryan_mtg.servobot.tournament;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class RecordCount {
    private Record record;
    private int count;
}
