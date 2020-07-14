package com.ryan_mtg.servobot.tournament.channelfireball.mfo.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Standings {
    String version;
    String status;

    @JsonProperty("current_round")
    int currentRound;

    @JsonProperty("last_updated")
    String lastUpdated;

    List<PlayerStanding> data;
}
