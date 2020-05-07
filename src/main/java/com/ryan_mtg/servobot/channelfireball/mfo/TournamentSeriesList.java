package com.ryan_mtg.servobot.channelfireball.mfo;

import lombok.Data;

import java.util.List;

@Data
public class TournamentSeriesList {
    String version;
    String status;
    String dataHash;
    List<TournamentSeries> data;
}
