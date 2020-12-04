package com.ryan_mtg.servobot.tournament.mtgmelee.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class TournamentJson {
    @JsonProperty("Name")
    private String name;

    @JsonProperty("ID")
    private int id;

    @JsonProperty("StartDate")
    private String startTime;

    @JsonProperty("Status")
    private String status;

    @JsonProperty("OrganizationName")
    private String organization;

    @JsonProperty("Format")
    private String format;
}
