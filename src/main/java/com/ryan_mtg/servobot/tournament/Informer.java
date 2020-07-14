package com.ryan_mtg.servobot.tournament;

import java.util.Map;

public interface Informer {
    String describeCurrentTournaments();
    String getCurrentDecklists();
    String getCurrentPairings();
    String getCurrentStandings();
    String getCurrentRound();
    String getCurrentRecords();
    Tournament getTournament(int tournamentId);
}
