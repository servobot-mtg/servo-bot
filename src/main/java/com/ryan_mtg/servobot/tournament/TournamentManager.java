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
        return mfoInformer.getTournaments();
    }

    public Tournament getTournament(final String name) {
        return mfoInformer.getTournament(name);
    }

    public Tournament getTournament(final int id) {
        return mfoInformer.getTournament(id);
    }
}
