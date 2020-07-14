package com.ryan_mtg.servobot.tournament.channelfireball.mfo.json;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PlayerStanding {
    String name;
    int points;
    int rank;
}
