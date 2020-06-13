package com.ryan_mtg.servobot.tournament;

import com.ryan_mtg.servobot.channelfireball.mfo.MfoInformer;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TournamentManager {
    private MfoInformer mfoInformer;

    public TournamentManager(final MfoInformer mfoInformer) {
        this.mfoInformer = mfoInformer;
    }

    public List<Tournament> getTournaments() {
        return mfoInformer.getCurrentTournaments().stream().map(this::convert).collect(Collectors.toList());
    }

    public Tournament getTournament(final String name) {
        return mfoInformer.getCurrentTournaments().stream()
                .filter(t -> t.getName().equalsIgnoreCase(name) || getNickName(t.getName()).equalsIgnoreCase(name))
                .map(this::convert).findFirst().get();
    }

    private Tournament convert(final com.ryan_mtg.servobot.channelfireball.mfo.json.Tournament tournament) {
        Tournament result = new Tournament(mfoInformer, tournament.getName(), tournament.getId());
        result.setRound(tournament.getCurrentRound());
        result.setPairingsUrl(mfoInformer.getPairingsUrl(tournament));
        result.setNickName(getNickName(tournament.getName()));
        result.setStandingsUrl(mfoInformer.getStandingsUrl(tournament));
        result.setDecklistUrl(mfoInformer.getDecklistsUrl(tournament));
        result.setStandings(mfoInformer.computeStandings(tournament));
        return result;
    }

    private String getNickName(final String name) {
        switch (name) {
            case "Players Tour - June 13th 12:00 AM PDT (00:00)":
                return "Players Tour Online 1";
            case "Players Tour - June 13th 09:00 AM PDT (09:00)":
                return "Players Tour Online 2";
        }
        return name;
    }
}
