package com.ryan_mtg.servobot.game.sus.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor
public class WeightedResponse {
    private int weight;
    private String response;
}
