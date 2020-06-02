package com.ryan_mtg.servobot.model.game_queue;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class GameQueueEntry {
    private int userId;
    private int spot;
    private int position;
}
