package com.ryan_mtg.servobot.tournament;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public interface Informer {
    String NO_ACTIVE_TOURNAMENTS = "There are no active tournaments.";

    List<Tournament> getTournaments();
    String describeCurrentTournaments();
    String getCurrentDecklists();
    String getCurrentPairings();
    String getCurrentStandings();
    String getCurrentRound();
    String getCurrentRecords();
    String getCurrentStatus(final String name);
    String getCurrentRecord(final String name);
    String getCurrentDecklist(final String name);
    Tournament getTournament(int tournamentId);

    static <TournamentType> String describeTournaments(final Collection<TournamentType> tournaments,
            final Function<TournamentType, String> nickNameGetter, final Function<TournamentType, String> function,
            final boolean showHeader, final boolean showPunctuation, final String emptyTournamentMessage) {
        Map<TournamentType, String> valueMap = new HashMap<>();
        List<TournamentType> currentTournaments = new ArrayList<>();
        tournaments.forEach(tournament -> {
            String value = function.apply(tournament);
            if (value != null) {
                valueMap.put(tournament, value);
                currentTournaments.add(tournament);
            }
        });

        StringBuilder builder = new StringBuilder();
        if (currentTournaments.size() > 1) {
            int seen = 0;
            for (TournamentType tournament : currentTournaments) {
                seen++;
                if (showHeader) {
                    builder.append(nickNameGetter.apply(tournament)).append(": ");
                }
                builder.append(valueMap.get(tournament));
                if (seen + 1 == currentTournaments.size()) {
                    if (showPunctuation && currentTournaments.size() > 2) {
                        builder.append(',');
                    }
                    builder.append(" and ");
                } else if (seen == currentTournaments.size()) {
                    if (showPunctuation) {
                        builder.append('.');
                    }
                } else {
                    if (showPunctuation) {
                        builder.append(", ");
                    } else {
                        builder.append(" ");
                    }
                }
            }
        } else if (currentTournaments.size() == 1) {
            TournamentType tournament = currentTournaments.get(0);
            if (showHeader) {
                builder.append(nickNameGetter.apply(tournament)).append(": ");
            }
            builder.append(valueMap.get(tournament));
            if (showPunctuation) {
                builder.append(".");
            }
        } else {
            return emptyTournamentMessage;
        }
        return builder.toString();
    }
}
