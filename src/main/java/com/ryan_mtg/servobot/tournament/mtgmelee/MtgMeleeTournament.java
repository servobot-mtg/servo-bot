package com.ryan_mtg.servobot.tournament.mtgmelee;

import com.ryan_mtg.servobot.tournament.TournamentType;
import lombok.Data;

import java.time.Instant;
import java.util.Map;

@Data
public class MtgMeleeTournament {
    private int id;
    private String name;
    private int standingsId;
    private Map<Integer, Integer> pairingsIdMap;
    private Instant startTime;
    private TournamentType tournamentType;
}