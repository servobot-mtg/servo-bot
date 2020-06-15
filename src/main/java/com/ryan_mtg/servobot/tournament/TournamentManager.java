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
        return mfoInformer.getCurrentTournaments(true).stream().map(mfoInformer::convert)
                .collect(Collectors.toList());
    }

    public Tournament getTournament(final String name) {
        return mfoInformer.getCurrentTournaments(true).stream().filter(t -> t.getName().equalsIgnoreCase(name)
                || MfoInformer.getNickName(t).equalsIgnoreCase(name))
                .map(mfoInformer::convert).findFirst().get();
    }
}
