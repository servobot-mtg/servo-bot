package com.ryan_mtg.servobot.channelfireball.mfo;

import com.ryan_mtg.servobot.channelfireball.mfo.json.Pairing;
import com.ryan_mtg.servobot.channelfireball.mfo.json.Pairings;
import com.ryan_mtg.servobot.channelfireball.mfo.json.PlayerStanding;
import com.ryan_mtg.servobot.channelfireball.mfo.json.Tournament;
import com.ryan_mtg.servobot.channelfireball.mfo.json.TournamentList;
import com.ryan_mtg.servobot.channelfireball.mfo.json.TournamentSeries;
import com.ryan_mtg.servobot.channelfireball.mfo.json.TournamentSeriesList;
import com.ryan_mtg.servobot.channelfireball.mfo.model.Player;
import com.ryan_mtg.servobot.channelfireball.mfo.model.PlayerSet;
import com.ryan_mtg.servobot.channelfireball.mfo.model.Record;
import com.ryan_mtg.servobot.channelfireball.mfo.model.Standings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Component
public class MfoInformer {
    private MfoClient mfoClient;
    private Clock clock;

    public MfoInformer() {
        this(MfoClient.newClient(), Clock.systemUTC());
    }

    public MfoInformer(final MfoClient mfoClient, final Clock clock) {
        this.mfoClient = mfoClient;
        this.clock = clock;
    }

    public List<Tournament> getCurrentTournaments() {
        TournamentSeriesList seriesList = mfoClient.getTournamentSeriesList();
        List<Tournament> tournaments = new ArrayList<>();
        for (TournamentSeries series : seriesList.getData()) {
            ZoneId zoneId = ZoneId.of(series.getTimezone());
            Instant startTime = parse(series.getStartDate(), zoneId);
            Instant endTime = parse(series.getEndDate(), zoneId);
            Instant now = clock.instant();
            if (startTime.compareTo(now) < 0 &&
                    (now.compareTo(endTime) < 0 || series.getName().contains("MagicFest Online"))) {
                tournaments.addAll(getCurrentTournaments(zoneId, series.getId()));
            }
        }
        return tournaments;
    }

    public String describeCurrentTournaments() {
        return describeTournaments(tournament -> tournament.getName(), false, true);
    }

    public String getCurrentDecklists() {
        return describeTournaments(
                tournament -> resolve(String.format("/deck/%d", tournament.getId())), true,false);
    }

    public String getCurrentPairings() {
        return describeTournaments(
                tournament -> resolve(String.format("/pairings/%d", tournament.getId())), true,false);
    }

    public String getCurrentStandings() {
        return describeTournaments(
                tournament -> resolve(String.format("/standings/%d", tournament.getId())), true, false);
    }

    public String getCurrentRound() {
        return describeTournaments(
                tournament -> String.format("round %d", tournament.getCurrentRound()), true, false);
    }

    public String getCurrentRecords() {
        return describeTournaments(tournament -> {
            Pairings pairings = mfoClient.getPairings(tournament.getId());
            Standings standings = computeStandings(pairings, getMaxRounds(tournament));
            Map<Record, Integer> recordCountMap = standings.getRecordCounts(3);
            return print(recordCountMap);
        }, true, true);
    }

    private Standings computeStandings(final Pairings pairings, final int maxRounds) {
        int round = Math.min(maxRounds, pairings.getCurrentRound() - 1);
        int maxPoints = 0;
        for (Pairing pairing : pairings.getData()) {
            maxPoints = Math.max(maxPoints, pairing.getPlayer().getPoints());
        }

        if (maxPoints > round * 3) {
            round++;
        }

        PlayerSet playerSet = new PlayerSet();
        Standings standings = new Standings(playerSet, round);
        for (Pairing pairing : pairings.getData()) {
            PlayerStanding playerStanding = pairing.getPlayer();
            Player player = Player.createFromMfoName(playerStanding.getName());
            standings.add(player, Record.newRecord(playerStanding.getPoints(), round));
        }

        return standings;
    }

    private String print(final Map<Record, Integer> recordCountMap) {
        List<Record> records = new ArrayList<>(recordCountMap.keySet());
        Collections.sort(records);
        Collections.reverse(records);
        StringBuilder stringBuilder = new StringBuilder();
        boolean first = true;
        for (Record record : records) {
            if (!first) {
                stringBuilder.append(", ");
            }
            int players = recordCountMap.get(record);
            stringBuilder.append(record).append(": ").append(players).append(players == 1 ? " player" : " players");
            first = false;
        }
        return stringBuilder.toString();
    }

    private List<Tournament> getCurrentTournaments(final ZoneId zoneId, final int tournamentSeriesId) {
        TournamentList tournamentList = mfoClient.getTournamentList(tournamentSeriesId);
        List<Tournament> tournaments = new ArrayList<>();
        for (Tournament tournament : tournamentList.getData()) {
            Instant startTime = parse(tournament.getStartsAt(), zoneId);
            Instant lastUpdatedTime = parse(tournament.getLastUpdated());
            Instant now = clock.instant();
            if (startTime.compareTo(now) < 0
                    && now.compareTo(lastUpdatedTime.plus(90, ChronoUnit.MINUTES)) < 0) {
                tournaments.add(tournament);
            }
        }
        return tournaments;
    }

    private int getMaxRounds(final Tournament tournament) {
        switch (tournament.getTournamentType()) {
            case "Grand Prix":
                if (tournament.getName().endsWith("Finals")) {
                    return 14;
                }
                return 15;
            case "Featured Tournament":
                if (tournament.getName().contains("Finals Qualifier")) {
                    return 5;
                }
                return 6;
        }

        throw new IllegalStateException("Unknown number of rounds for " + tournament.getName());
    }

    private String describeTournaments(final Function<Tournament, String> function, final boolean showHeader,
            final boolean showPunctuation) {
        List<Tournament> tournaments = getCurrentTournaments();
        StringBuilder builder = new StringBuilder();
        if (tournaments.size() > 1) {
            int seen = 0;
            for (Tournament tournament : tournaments) {
                seen++;
                if (showHeader) {
                    builder.append(tournament.getName()).append(": ");
                }
                builder.append(function.apply(tournament));
                if (seen + 1 == tournaments.size()) {
                    if (showPunctuation && tournaments.size() > 2) {
                        builder.append(',');
                    }
                    builder.append(" and ");
                } else if (seen == tournaments.size()) {
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
        } else if (tournaments.size() == 1) {
            Tournament tournament = tournaments.get(0);
            if (showHeader) {
                builder.append(tournament.getName()).append(": ");
            }
            builder.append(function.apply(tournament));
            if (showPunctuation) {
                builder.append(".");
            }
        } else {
            return "There are no active tournaments.";
        }
        return builder.toString();
    }

    private String resolve(final String path) {
        return MfoClient.getServerUrl() + path;
    }

    private Instant parse(final String time) {
        return LocalDateTime.parse(time, DateTimeFormatter.ISO_OFFSET_DATE_TIME).toInstant(ZoneOffset.UTC);
    }

    private Instant parse(final String time, final ZoneId zoneId) {
        return LocalDateTime.parse(time, DateTimeFormatter.ISO_OFFSET_DATE_TIME).atZone(zoneId).toInstant();
    }
}
