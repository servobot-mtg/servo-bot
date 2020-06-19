package com.ryan_mtg.servobot.tournament;

import com.ryan_mtg.servobot.channelfireball.mfo.MfoInformer;
import com.ryan_mtg.servobot.utility.Time;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.Instant;
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

    @Getter @Setter
    private Instant startTime;

    @Getter @Setter
    private TournamentType type;

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

    public List<PlayerStanding> getPlayers() {
        List<PlayerStanding> players = new ArrayList<>();
        PlayerSet playerSet = standings.getPlayerSet();
        mergeCareAbouts(playerSet, CARE_ABOUTS);
        Map<Player, DecklistDescription> decklistMap = mfoInformer.parseDecklistsFor(standings.getPlayerSet(), id);

        Record leaderRecord = getLeaderRecord();

        for (Player player : playerSet) {
            Player opponent = pairings.getOpponent(player);
            players.add(new PlayerStanding(player, standings.getRank(player), isWatchable(player),
                isLeader(leaderRecord, player), standings.getRecord(player), decklistMap.get(player), opponent,
                decklistMap.get(opponent)));
        }

        Collections.sort(players);
        return players;
    }

    public boolean hasStarted() {
        return Instant.now().compareTo(startTime) >= 0;
    }

    public String getTimeUntilStart() {
        return Time.toReadableString(Duration.between(Instant.now(), startTime));
    }

    private void mergeCareAbouts(final PlayerSet playerSet, final Set<Player> careAbouts) {
        for (Player careAboutPlayer : careAbouts) {
            if (playerSet.findByArenaName(careAboutPlayer.getArenaName()) != null) {
                playerSet.merge(careAboutPlayer);
            }
        }
    }

    private boolean isLeader(final Record leaderRecord, final Player player) {
        if (leaderRecord == null) {
            return false;
        }

        Record playerRecord = standings.getRecord(player);
        return playerRecord.compareTo(leaderRecord) >= 0;
    }

    private boolean isWatchable(final Player player) {
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
