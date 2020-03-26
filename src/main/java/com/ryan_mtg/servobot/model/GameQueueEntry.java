package com.ryan_mtg.servobot.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class GameQueueEntry {
    @Getter
    private int userId;

    @Getter
    private int spot;

    @Getter
    private int position;
}
