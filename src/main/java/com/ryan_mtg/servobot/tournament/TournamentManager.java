package com.ryan_mtg.servobot.tournament;

import com.ryan_mtg.servobot.tournament.channelfireball.mfo.MfoInformer;
import com.ryan_mtg.servobot.tournament.mtgmelee.MtgMeleeInformer;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TournamentManager {
    private MfoInformer mfoInformer;
    private MtgMeleeInformer meleeInformer;

    public TournamentManager(final MfoInformer mfoInformer, final MtgMeleeInformer meleeInformer) {
        this.mfoInformer = mfoInformer;
        this.meleeInformer = meleeInformer;
    }

    public List<Tournament> getTournaments() {
        return mfoInformer.getTournaments();
    }

    public Tournament getTournament(final String name) {
        return mfoInformer.getTournament(name);
    }

    public Tournament getCfbTournament(final int id) {
        return mfoInformer.getTournament(id);
    }

    public Tournament getScgTournament(final int id) {
        return meleeInformer.getTournament(id);
    }
}
