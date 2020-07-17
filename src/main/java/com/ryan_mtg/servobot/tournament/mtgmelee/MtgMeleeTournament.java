package com.ryan_mtg.servobot.tournament.mtgmelee;

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
}