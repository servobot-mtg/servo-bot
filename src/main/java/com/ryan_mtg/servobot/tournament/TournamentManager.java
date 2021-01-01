package com.ryan_mtg.servobot.tournament;

import java.util.List;

public interface TournamentManager {
    List<Tournament> getTournaments();
    Tournament getTournament(final String name);
    Tournament getCfbTournament(final int id);
    Tournament getScgTournament(final int id);
}
