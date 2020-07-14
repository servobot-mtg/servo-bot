package com.ryan_mtg.servobot.tournament.channelfireball.mfo.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TournamentSeries {
    int id;
    String name;
    String hashtag;
    String timezone;

    @JsonProperty("start_date")
    String startDate;

    @JsonProperty("end_date")
    String endDate;

    @JsonProperty("url_tournament_list")
    String tournamentListUrl;
}
