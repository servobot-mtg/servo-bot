package com.ryan_mtg.servobot.tournament;

import com.ryan_mtg.servobot.tournament.mtgmelee.MtgMeleeInformer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

//@Component
public class InMemoryTournamentManager implements TournamentManager {
    private final List<Tournament> tournaments = new ArrayList<>();
    private final Map<Integer, Tournament> byIdMap = new HashMap<>();
    private final MtgMeleeInformer meleeInformer;
    private final ScheduledExecutorService executor;

    public InMemoryTournamentManager(final MtgMeleeInformer meleeInformer) {
        this.meleeInformer = meleeInformer;

        executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(this::findTournaments, 0, 30, TimeUnit.MINUTES);
        executor.scheduleAtFixedRate(this::updateTournaments, 1, 1, TimeUnit.MINUTES);
    }

    @Override
    public List<Tournament> getTournaments() {
        return tournaments;
    }

    @Override
    public Tournament getTournament(final String name) {
        return tournaments.stream().filter(tournament
            -> tournament.getName().equalsIgnoreCase(name) || tournament.getNickName().equalsIgnoreCase(name))
            .findFirst().orElse(null);
    }

    @Override
    public Tournament getCfbTournament(final int id) {
        return null;
    }

    @Override
    public Tournament getScgTournament(final int id) {
        return byIdMap.get(id);
    }

    public void findTournaments() {
         List<Tournament> foundTournaments = meleeInformer.findTournaments(this);
        tournaments.addAll(foundTournaments);
        foundTournaments.forEach(tournament -> byIdMap.put(tournament.getId(), tournament));
    }

    public void updateTournaments() {

    }
}