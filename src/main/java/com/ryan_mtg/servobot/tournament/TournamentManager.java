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
        return mfoInformer.getCurrentTournaments().stream().map(mfoInformer::convert)
                .collect(Collectors.toList());
    }

    public Tournament getTournament(final String name) {
        return mfoInformer.getCurrentTournaments().stream().filter(t -> t.getName().equalsIgnoreCase(name)
                || MfoInformer.getNickName(t).equalsIgnoreCase(name))
                .map(mfoInformer::convert).findFirst().get();
    }

    public Tournament getTournament(final int id) {
        return mfoInformer.getCurrentTournaments().stream().filter(t -> t.getId() == id)
                .map(mfoInformer::convert).findFirst().get();
    }
}
