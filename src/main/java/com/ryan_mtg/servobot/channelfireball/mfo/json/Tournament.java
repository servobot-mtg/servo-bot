package com.ryan_mtg.servobot.channelfireball.mfo.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Tournament {
    int id;
    String name;
    String format;

    @JsonProperty("starts_at")
    String startsAt;

    @JsonProperty("tournament_type")
    String tournamentType;

    @JsonProperty("registration_fee")
    String registrationFee;

    @JsonProperty("allow_decklists")
    boolean allowDecklists;

    @JsonProperty("current_round")
    int currentRound;

    @JsonProperty("timer_state")
    int timerState;

    @JsonProperty("last_updated_at")
    String lastUpdated;

    @JsonProperty("decklist_submit_url")
    String decklistSubmitUrl;

    @JsonProperty("pairings_url")
    String pairingsUrl;

    @JsonProperty("standings_url")
    String standingsUrl;

    @JsonProperty("pairings_last_updated_at")
    String pairingsLastUpdated;

    @JsonProperty("decklist_list_url")
    String decklistListUrl;
}
