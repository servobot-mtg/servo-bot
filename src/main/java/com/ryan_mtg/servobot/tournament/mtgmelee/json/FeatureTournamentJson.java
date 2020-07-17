package com.ryan_mtg.servobot.tournament.mtgmelee.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class FeatureTournamentJson {
    private String name;

    private int id;

    @JsonProperty("startDate")
    private String startTime;
}
