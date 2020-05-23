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
import org.apache.commons.codec.Charsets;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Slf4j
@Component
public class MfoInformer {
    private static final String NO_ACTIVE_TOURNAMENTS = "There are no active tournaments.";

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
            Instant endTime = parse(series.getEndDate(), zoneId).plus(1, ChronoUnit.DAYS);
            Instant now = clock.instant();
            if (startTime.compareTo(now) < 0 &&
                    (now.compareTo(endTime) < 0 || series.getName().contains("MagicFest Online"))) {
                tournaments.addAll(getCurrentTournaments(zoneId, series.getId()));
            }
        }
        return tournaments;
    }

    public String describeCurrentTournaments() {
        return describeTournaments(Tournament::getName, false, true, NO_ACTIVE_TOURNAMENTS);
    }

    public String getCurrentDecklists() {
        return describeTournaments(tournament -> resolve(String.format("/deck/%d", tournament.getId())), true,
                false, NO_ACTIVE_TOURNAMENTS);
    }

    public String getCurrentDecklist(final String arenaName) {
        return describeTournaments(tournament -> {
            Standings standings = computeStandings(tournament);
            Player player = standings.getPlayerSet().findByArenaName(arenaName);
            if (player == null) {
                return null;
            }
            return parseDecklistsFor(player, tournament.getId());
        }, true, false, String.format("%s is not in the tournament.", arenaName));
    }

    public String getCurrentPairings() {
        return describeTournaments(tournament -> resolve(String.format("/pairings/%d", tournament.getId())), true,
                false, NO_ACTIVE_TOURNAMENTS);
    }

    public String getCurrentStandings() {
        return describeTournaments( tournament -> resolve(String.format("/standings/%d", tournament.getId())), true,
                false, NO_ACTIVE_TOURNAMENTS);
    }

    public String getCurrentRound() {
        return describeTournaments(tournament -> String.format("round %d", tournament.getCurrentRound()), true,
                false, NO_ACTIVE_TOURNAMENTS);
    }

    public String getCurrentRecords() {
        return describeTournaments(tournament -> {
            Standings standings = computeStandings(tournament);
            Map<Record, Integer> recordCountMap = standings.getRecordCounts(3);
            return print(recordCountMap);
        }, true, true, NO_ACTIVE_TOURNAMENTS);
    }

    public String getCurrentRecord(final String arenaName) {
        return describeTournaments(tournament -> {
            Standings standings = computeStandings(tournament);
            Player player = standings.getPlayerSet().findByArenaName(arenaName);
            if (player == null) {
                return null;
            }
            return standings.getRecord(player).toString();
        }, true, true, String.format("There are no current tournaments for %s.", arenaName));
    }

    private Standings computeStandings(final Tournament tournament) {
        int tournamentId = tournament.getId();
        Pairings pairings = mfoClient.getPairings(tournamentId);
        int maxRounds = getMaxRounds(tournament);
        if (!pairings.getData().isEmpty()) {
            return computeStandings(pairings, maxRounds);
        }

        com.ryan_mtg.servobot.channelfireball.mfo.json.Standings standingsJson = mfoClient.getStandings(tournamentId);
        return computeStandings(standingsJson, maxRounds);
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

    private Standings computeStandings(final com.ryan_mtg.servobot.channelfireball.mfo.json.Standings standingsJson,
            final int maxRounds) {
        int round = Math.min(maxRounds, standingsJson.getCurrentRound() - 1);
        int maxPoints = 0;
        for (PlayerStanding playerStanding : standingsJson.getData()) {
            maxPoints = Math.max(maxPoints, playerStanding.getPoints());
        }

        if (maxPoints > round * 3) {
            round++;
        }

        PlayerSet playerSet = new PlayerSet();
        Standings standings = new Standings(playerSet, round);
        for (PlayerStanding playerStanding : standingsJson.getData()) {
            Player player = Player.createFromMfoName(playerStanding.getName());
            standings.add(player, Record.newRecord(playerStanding.getPoints(), round));
        }

        return standings;
    }

    private String parseDecklistsFor(final Player player, final int tournamentId) {
        try {
            String url = String.format("https://my.cfbevents.com/deck/%d", tournamentId);
            HttpClient httpClient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(url);
            HttpResponse response = httpClient.execute(httpGet);

            Document document = Jsoup.parse(response.getEntity().getContent(), Charsets.UTF_8.name(), url);

            List<String> results = new ArrayList<>();
            document.select("tr").forEach(row -> {
                if(!row.parent().nodeName().equals("thead")) {
                    if (row.child(0).text().equals(player.getArenaName())) {
                        Element anchor = row.child(2).child(0);
                        results.add(anchor.attr("href"));
                    }
                }
            });

            if (!results.isEmpty()) {
                return results.get(0);
            }
            return null;
        } catch (IOException e) {
            return null;
        }
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
            Instant now = clock.instant();
            if (startTime.compareTo(now) < 0 && !hasEnded(tournament, now)) {
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
                } else if (tournament.getName().contains("Showdown")) {
                    return 8;
                }
                return 6;
            case "Package":
            case "Players Tour":
            case "Select Your Playmat":
            case "MagicFest In-A-Box":
                return 0;
        }

        LOGGER.warn("Unknown tournament type: " + tournament.getTournamentType());
        return 6; //This could be very, very wrong
    }

    private boolean hasEnded(final Tournament tournament, final Instant now) {
        if (tournament.getCurrentRound() < getMaxRounds(tournament)) {
            return false;
        }

        Instant lastUpdatedTime = parse(tournament.getLastUpdated());
        return now.compareTo(lastUpdatedTime.plus(90, ChronoUnit.MINUTES)) > 0;
    }

    private String describeTournaments(final Function<Tournament, String> function, final boolean showHeader,
            final boolean showPunctuation, final String emptyTournamentMessage) {
        Map<Tournament, String> valueMap = new HashMap<>();
        List<Tournament> tournaments = new ArrayList<>();
        getCurrentTournaments().forEach(tournament -> {
            String value = function.apply(tournament);
            if (value != null) {
                valueMap.put(tournament, value);
                tournaments.add(tournament);
            }
        });

        StringBuilder builder = new StringBuilder();
        if (tournaments.size() > 1) {
            int seen = 0;
            for (Tournament tournament : tournaments) {
                seen++;
                if (showHeader) {
                    builder.append(tournament.getName()).append(": ");
                }
                builder.append(valueMap.get(tournament));
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
            builder.append(valueMap.get(tournament));
            if (showPunctuation) {
                builder.append(".");
            }
        } else {
            return emptyTournamentMessage;
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
