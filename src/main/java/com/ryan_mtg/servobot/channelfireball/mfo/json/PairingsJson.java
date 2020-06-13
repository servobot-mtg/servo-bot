package com.ryan_mtg.servobot.channelfireball.mfo.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PairingsJson {
    String version;
    String status;

    @JsonProperty("current_round")
    int currentRound;

    @JsonProperty("last_updated")
    String lastUpdated;

    List<Pairing> data;
}
