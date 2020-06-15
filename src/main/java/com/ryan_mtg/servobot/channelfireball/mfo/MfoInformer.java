package com.ryan_mtg.servobot.channelfireball.mfo;

import com.ryan_mtg.servobot.channelfireball.mfo.json.Pairing;
import com.ryan_mtg.servobot.channelfireball.mfo.json.PairingsJson;
import com.ryan_mtg.servobot.channelfireball.mfo.json.PlayerStanding;
import com.ryan_mtg.servobot.channelfireball.mfo.json.Tournament;
import com.ryan_mtg.servobot.channelfireball.mfo.json.TournamentList;
import com.ryan_mtg.servobot.channelfireball.mfo.json.TournamentSeries;
import com.ryan_mtg.servobot.channelfireball.mfo.json.TournamentSeriesList;
import com.ryan_mtg.servobot.channelfireball.mfo.model.DecklistDescription;
import com.ryan_mtg.servobot.channelfireball.mfo.model.Pairings;
import com.ryan_mtg.servobot.channelfireball.mfo.model.Player;
import com.ryan_mtg.servobot.channelfireball.mfo.model.PlayerSet;
import com.ryan_mtg.servobot.channelfireball.mfo.model.Record;
import com.ryan_mtg.servobot.channelfireball.mfo.model.RecordCount;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class MfoInformer {
    private static final String NO_ACTIVE_TOURNAMENTS = "There are no active tournaments.";

    private MfoClient mfoClient;
    private Clock clock;
    private Map<String, String> decklistNameCache = new HashMap<>();

    public MfoInformer() {
        this(MfoClient.newClient(), Clock.systemUTC());
    }

    public MfoInformer(final MfoClient mfoClient, final Clock clock) {
        this.mfoClient = mfoClient;
        this.clock = clock;
    }

    public List<Tournament> getCurrentTournaments() {
       return getCurrentTournaments(false);
    }

    public List<Tournament> getCurrentTournaments(final boolean includeIdle) {
        TournamentSeriesList seriesList = mfoClient.getTournamentSeriesList();
        List<Tournament> tournaments = new ArrayList<>();
        for (TournamentSeries series : seriesList.getData()) {
            ZoneId zoneId = ZoneId.of(series.getTimezone());
            Instant startTime = parse(series.getStartDate(), zoneId);
            Instant endTime = parse(series.getEndDate(), zoneId).plus(2, ChronoUnit.DAYS);
            Instant now = clock.instant();
            if (startTime.compareTo(now) < 0 &&
                    (now.compareTo(endTime) < 0 || series.getName().contains("MagicFest Online"))) {
                tournaments.addAll(getCurrentTournaments(zoneId, series.getId(), includeIdle));
            }
        }
        return filterByTournamentLevel(tournaments);
    }

    public String describeCurrentTournaments() {
        return describeTournaments(MfoInformer::getNickName, false, true, NO_ACTIVE_TOURNAMENTS);
    }

    public String getCurrentDecklists() {
        return describeTournaments(this::getDecklistsUrl, true, false, NO_ACTIVE_TOURNAMENTS);
    }

    public String getDecklistsUrl(final Tournament tournament) {
        return resolve(String.format("/deck/%d", tournament.getId()));
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
        return describeTournaments(this::getPairingsUrl, true, false, NO_ACTIVE_TOURNAMENTS);
    }

    public String getPairingsUrl(final Tournament tournament) {
        return resolve(String.format("/pairings/%d", tournament.getId()));
    }

    public String getCurrentStandings() {
        return describeTournaments(this::getStandingsUrl, true, false, NO_ACTIVE_TOURNAMENTS);
    }

    public String getStandingsUrl(final Tournament tournament) {
        return resolve(String.format("/standings/%d", tournament.getId()));
    }

    public String getCurrentRound() {
        return describeTournaments(tournament -> String.format("round %d", tournament.getCurrentRound()), true,
                false, NO_ACTIVE_TOURNAMENTS);
    }

    public String getCurrentRecords() {
        return describeTournaments(tournament -> {
            Standings standings = computeStandings(tournament);
            return print(standings.getRecordCounts(3));
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
        PairingsJson pairings = mfoClient.getPairings(tournamentId);
        return computeStandings(tournament, pairings);
    }

    private Standings computeStandings(final Tournament tournament, final PairingsJson pairings) {
        int maxRounds = getMaxRounds(tournament);
        if (!pairings.getData().isEmpty()) {
            return computeStandings(tournament.getId(), pairings, maxRounds);
        }

        int tournamentId = tournament.getId();
        com.ryan_mtg.servobot.channelfireball.mfo.json.Standings standingsJson = mfoClient.getStandings(tournamentId);
        return computeStandings(standingsJson, maxRounds);
    }

    private Standings computeStandings(final int tournamentId, final PairingsJson pairings, final int maxRounds) {
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

        com.ryan_mtg.servobot.channelfireball.mfo.json.Standings standingsJson = mfoClient.getStandings(tournamentId);
        for (PlayerStanding playerStanding : standingsJson.getData()) {
            Player player = playerSet.findByArenaName(Player.createFromMfoName(playerStanding.getName()).getArenaName());
            standings.setRank(player, playerStanding.getRank());
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
            standings.setRank(player, playerStanding.getRank());
        }

        return standings;
    }

    public Map<Player, DecklistDescription> parseDecklistsFor(final PlayerSet players, final int tournamentId) {
        try {
            String url = String.format("https://my.cfbevents.com/deck/%d", tournamentId);
            HttpClient httpClient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(url);
            HttpResponse response = httpClient.execute(httpGet);

            Document document = Jsoup.parse(response.getEntity().getContent(), Charsets.UTF_8.name(), url);

            Map<Player, DecklistDescription> decklistDescriptionMap = new HashMap<>();
            document.select("tr").forEach(row -> {
                if(!row.parent().nodeName().equals("thead")) {
                    String arenaName = row.child(0).text();
                    Player player = players.findByArenaName(arenaName);
                    if (player != null) {
                        Element anchor = row.child(2).child(0);
                        String decklistUrl = anchor.attr("href");
                        String decklistName = getDecklistName(decklistUrl);
                        decklistDescriptionMap.put(player, new DecklistDescription(decklistName, decklistUrl));
                    }
                }
            });

            return decklistDescriptionMap;
        } catch (IOException e) {
            return null;
        }
    }

    public com.ryan_mtg.servobot.tournament.Tournament convert(final Tournament tournament) {
        com.ryan_mtg.servobot.tournament.Tournament result =
                new com.ryan_mtg.servobot.tournament.Tournament(this, tournament.getName(), tournament.getId());
        result.setRound(tournament.getCurrentRound());
        result.setPairingsUrl(getPairingsUrl(tournament));
        result.setNickName(getNickName(tournament));
        result.setStandingsUrl(getStandingsUrl(tournament));
        result.setDecklistUrl(getDecklistsUrl(tournament));

        int tournamentId = tournament.getId();
        PairingsJson pairings = mfoClient.getPairings(tournamentId);
        Standings standings = computeStandings(tournament, pairings);
        PlayerSet playerSet = standings.getPlayerSet();
        result.setStandings(standings);

        result.setPairings(computePairings(tournament, pairings, playerSet));
        return result;
    }

    private Pairings computePairings(final Tournament tournament, final PairingsJson pairingsJson,
            final PlayerSet playerSet) {
        Pairings pairings = new Pairings(playerSet, pairingsJson.getCurrentRound());

        for (Pairing pairing : pairingsJson.getData()) {
            PlayerStanding playerStanding = pairing.getPlayer();
            Player player = Player.createFromMfoName(playerStanding.getName());
            Player opponent = Player.createFromMfoName(pairing.getOpponent().getName());
            pairings.add(player, opponent);
        }

        return pairings;
    }

    public static String getNickName(final Tournament tournament) {
        switch (tournament.getName()) {
            case "Players Tour - June 13th 12:00 AM PDT (00:00)":
                return "Players Tour Online 1";
            case "Players Tour - June 13th 09:00 AM PDT (09:00)":
                return "Players Tour Online 2";
        }
        return tournament.getName();
    }

    private String parseDecklistsFor(final Player player, final int tournamentId) {
        try {
            String url = String.format("https://my.cfbevents.com/deck/%d", tournamentId);
            HttpClient httpClient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(url);
            HttpResponse response = httpClient.execute(httpGet);

            Document document = Jsoup.parse(response.getEntity().getContent(), Charsets.UTF_8.name(), url);

            List<String> urls = new ArrayList<>();
            document.select("tr").forEach(row -> {
                if(!row.parent().nodeName().equals("thead")) {
                    if (row.child(0).text().equals(player.getArenaName())) {
                        Element anchor = row.child(2).child(0);
                        urls.add(anchor.attr("href"));
                    }
                }
            });

            if (!urls.isEmpty()) {
                String decklistUrl = urls.get(0);
                String decklistName = getDecklistName(decklistUrl);
                if (decklistName == null) {
                    return decklistUrl;
                }
                return String.format("%s %s", decklistName, decklistUrl);
            }
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    private String getDecklistName(final String decklistUrl) {
        try {
            if (decklistNameCache.containsKey(decklistUrl)) {
                return decklistNameCache.get(decklistUrl);
            }

            String url = decklistUrl;
            HttpClient httpClient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(url);
            HttpResponse response = httpClient.execute(httpGet);

            Document document = Jsoup.parse(response.getEntity().getContent(), Charsets.UTF_8.name(), url);

            List<String> headers = new ArrayList<>();
            document.select("h1").forEach(header -> headers.add(header.text()));

            if (headers.size() == 3) {
                String name = headers.get(1);
                LOGGER.info("Saving deck {} for {}", name, decklistUrl);
                decklistNameCache.put(decklistUrl, name);
                return name;
            }
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    private String print(final List<RecordCount> recordCounts) {
        StringBuilder stringBuilder = new StringBuilder();
        boolean first = true;
        for (RecordCount recordCount : recordCounts) {
            if (!first) {
                stringBuilder.append(", ");
            }
            Record record = recordCount.getRecord();
            int players = recordCount.getCount();
            stringBuilder.append(record).append(": ").append(players).append(players == 1 ? " player" : " players");
            first = false;
        }
        return stringBuilder.toString();
    }

    private List<Tournament> filterByTournamentLevel(final List<Tournament> tournaments) {
        if (tournaments.isEmpty()) {
            return tournaments;
        }

        Tournament maxTournament = tournaments.get(0);
        for (int i = 1; i < tournaments.size(); i++) {
            maxTournament = maxType(maxTournament, tournaments.get(i));
        }

        int maxValue = typeValue(maxTournament);
        return tournaments.stream().filter(t -> typeValue(t) == maxValue).collect(Collectors.toList());
    }

    private Tournament maxType(final Tournament tournamentA, final Tournament tournamentB) {
        if (typeValue(tournamentA) >= typeValue(tournamentB)) {
            return tournamentA;
        }
        return tournamentB;
    }

    private int typeValue(final Tournament tournament) {
        switch (tournament.getTournamentType()) {
            case "Featured Tournament":
                if (tournament.getName().contains("Players Tour -")) {
                    return 10;
                }
                return 5;
            case "Players Tour":
            case "Grand Prix":
            case "Package":
            case "Select Your Playmat":
            case "MagicFest In-A-Box":
                return 0;
        }
        return 0;
    }

    private List<Tournament> getCurrentTournaments(final ZoneId zoneId, final int tournamentSeriesId,
            final boolean includeIdle) {
        TournamentList tournamentList = mfoClient.getTournamentList(tournamentSeriesId);
        List<Tournament> tournaments = new ArrayList<>();
        for (Tournament tournament : tournamentList.getData()) {
            Instant now = clock.instant();
            if (hasStarted(tournament, now, zoneId) && !hasEnded(tournament, now)
                    && (!isIdle(tournament, now) || includeIdle)) {
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
                if (tournament.getName().contains("Players Tour -")) {
                    return 15;
                } else if (tournament.getName().contains("Finals Qualifier")) {
                    return 5;
                } else if (tournament.getName().contains("Qualifier")) {
                    return 7; // really depends on player count
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

    private boolean hasStarted(final Tournament tournament, final Instant now, final ZoneId zoneId) {
        int maxRounds = getMaxRounds(tournament);
        if (tournament.getCurrentRound() == 0 || maxRounds == 0) {
            return false;
        }
        Instant startTime = parse(tournament.getStartsAt(), zoneId);
        return startTime.compareTo(now) < 0;
    }

    private boolean hasEnded(final Tournament tournament, final Instant now) {
        if (tournament.getCurrentRound() < getMaxRounds(tournament)) {
            return false;
        }

        Instant lastUpdatedTime = parse(tournament.getLastUpdated());
        return now.compareTo(lastUpdatedTime.plus(90, ChronoUnit.MINUTES)) > 0;
    }

    private boolean isIdle(final Tournament tournament, final Instant now) {
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
                    builder.append(getNickName(tournament)).append(": ");
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
                builder.append(getNickName(tournament)).append(": ");
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
