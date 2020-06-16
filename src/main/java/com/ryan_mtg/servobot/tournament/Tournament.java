package com.ryan_mtg.servobot.tournament;

import com.ryan_mtg.servobot.channelfireball.mfo.MfoInformer;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Tournament {
    private static final int LEADERS = 16;

    @Getter
    private String name;

    @Getter @Setter
    private String nickName;

    @Getter @Setter
    private int round;

    @Getter @Setter
    private String pairingsUrl;

    @Getter @Setter
    private String standingsUrl;

    @Getter @Setter
    private String decklistUrl;

    @Getter @Setter
    private Standings standings;

    @Getter @Setter
    private Pairings pairings;

    private MfoInformer mfoInformer;
    private int id;

    public Tournament(final MfoInformer informer, final String name, final int id) {
        this.mfoInformer = informer;
        this.name = name;
        this.id = id;
    }

    private static final Set<Player> CARE_ABOUTS = new HashSet<>(Arrays.asList(
        new Player("MZBlazer#72009", null, null, null, null, "MTGMilan"),
        new Player("Filipa#15754", null, null, null, "filipacarola", "filipamtg"),
        new Player("Booradley95#84650", null, null, null, null, "bradleyyoo_mtg"),
        new Player("Conanhawk#53621", null, "Eric Hawkins", null, "conanhawk", "conanhawk"),
        new Player("h0lydiva#65001", null, "Daniela Diaz", null, "h0lydiva", "h0lyDiva"),
        new Player("themightylinguine#94385", null, "Carolyn Kavanagh", "Moosers",
                "themightylinguine", "mightylinguine")
    ));

    public List<PlayerStanding> getPlayersToWatch() {
        List<PlayerStanding> playersToWatch = new ArrayList<>();
        PlayerSet playerSet = standings.getPlayerSet();
        mergeCareAbouts(playerSet, CARE_ABOUTS);
        Record leaderRecord = getLeaderRecord();

        Map<Player, DecklistDescription> decklistMap = mfoInformer.parseDecklistsFor(standings.getPlayerSet(), id);

        for (Player player : playerSet) {
            if (isWatchable(leaderRecord, player)) {
                Player opponent = pairings.getOpponent(player);
                playersToWatch.add(new PlayerStanding(player, standings.getRank(player), opponent,
                        standings.getRecord(player), decklistMap.get(player), decklistMap.get(opponent)));
            }
        }

        Collections.sort(playersToWatch);
        return playersToWatch;
    }

    public List<PlayerStanding> getPlayers() {
        List<PlayerStanding> players = new ArrayList<>();
        PlayerSet playerSet = standings.getPlayerSet();
        mergeCareAbouts(playerSet, CARE_ABOUTS);
        Map<Player, DecklistDescription> decklistMap = mfoInformer.parseDecklistsFor(standings.getPlayerSet(), id);

        for (Player player : playerSet) {
            Player opponent = pairings.getOpponent(player);
            players.add(new PlayerStanding(player, standings.getRank(player), opponent,
                    standings.getRecord(player), decklistMap.get(player), decklistMap.get(opponent)));
        }

        Collections.sort(players);
        return players;
    }

    private void mergeCareAbouts(final PlayerSet playerSet, final Set<Player> careAbouts) {
        for (Player careAboutPlayer : careAbouts) {
            if (playerSet.findByArenaName(careAboutPlayer.getArenaName()) != null) {
                playerSet.merge(careAboutPlayer);
            }
        }
    }

    private boolean isWatchable(final Record leaderRecord, final Player player) {
        Record playerRecord = standings.getRecord(player);
        if (leaderRecord != null && playerRecord.compareTo(leaderRecord) >= 0) {
            return true;
        }

        for (Player careAboutPlayer : CARE_ABOUTS) {
            if (player.getArenaName().equals(careAboutPlayer.getArenaName())) {
                return true;
            }
        }
        return false;
    }

    private Record getLeaderRecord() {
        List<RecordCount> recordCounts = standings.getRecordCounts(4);
        if (recordCounts.isEmpty() || recordCounts.get(0).getCount() > LEADERS) {
            return null;
        }

        int leaders = 0;
        int index = 0;
        Record bestRecord = recordCounts.get(0).getRecord();
        while (index < recordCounts.size() && leaders + recordCounts.get(index).getCount() <= LEADERS) {
            leaders += recordCounts.get(index).getCount();
            bestRecord = recordCounts.get(index).getRecord();
            index++;
        }

        return bestRecord;
    }
}
