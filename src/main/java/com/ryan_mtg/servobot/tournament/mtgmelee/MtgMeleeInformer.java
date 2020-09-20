package com.ryan_mtg.servobot.tournament.mtgmelee;

import com.ryan_mtg.servobot.tournament.DecklistDescription;
import com.ryan_mtg.servobot.tournament.Informer;
import com.ryan_mtg.servobot.tournament.Pairings;
import com.ryan_mtg.servobot.tournament.Player;
import com.ryan_mtg.servobot.tournament.PlayerSet;
import com.ryan_mtg.servobot.tournament.PlayerStanding;
import com.ryan_mtg.servobot.tournament.Record;
import com.ryan_mtg.servobot.tournament.RecordCount;
import com.ryan_mtg.servobot.tournament.Standings;
import com.ryan_mtg.servobot.tournament.Tournament;
import com.ryan_mtg.servobot.tournament.TournamentType;
import com.ryan_mtg.servobot.tournament.mtgmelee.json.PairingInfo;
import com.ryan_mtg.servobot.tournament.mtgmelee.json.PairingsJson;
import com.ryan_mtg.servobot.tournament.mtgmelee.json.PlayerInfo;
import com.ryan_mtg.servobot.tournament.mtgmelee.json.StandingsJson;
import com.ryan_mtg.servobot.tournament.mtgmelee.json.TournamentJson;
import com.ryan_mtg.servobot.tournament.mtgmelee.json.TournamentsJson;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class MtgMeleeInformer implements Informer {
    public static final String STANDINGS_ID = "standings";
    public static final String PAIRINGS_ID = "pairings";
    private MtgMeleeWebParser parser;
    private MtgMeleeClient client;
    private Clock clock;

    public MtgMeleeInformer() {
        this(new MtgMeleeWebParser(), MtgMeleeClient.newClient(), Clock.systemUTC());
    }

    public MtgMeleeInformer(final MtgMeleeWebParser parser, final MtgMeleeClient client, final Clock clock) {
        this.parser = parser;
        this.client = client;
        this.clock = clock;
    }

    @Override
    public List<Tournament> getTournaments() {
        return getCurrentTournaments(true).stream().map(meleeTournament -> getTournament(meleeTournament))
                .collect(Collectors.toList());
    }

    @Override
    public String describeCurrentTournaments() {
        return describeTournaments(MtgMeleeInformer::getNickName, false, true, NO_ACTIVE_TOURNAMENTS);
    }

    @Override
    public String getCurrentDecklists() {
        return describeTournaments(this::getDecklistsUrl, true, false, NO_ACTIVE_TOURNAMENTS);
    }

    @Override
    public String getCurrentPairings() {
        return describeTournaments(this::getPairingsUrl, true, false, NO_ACTIVE_TOURNAMENTS);
    }

    @Override
    public String getCurrentStandings() {
        return describeTournaments(this::getStandingsUrl, true, false, NO_ACTIVE_TOURNAMENTS);
    }

    @Override
    public String getCurrentRound() {
        return describeTournaments(tournament -> String.format("round %d", getCurrentRound(tournament)), true,
                false, NO_ACTIVE_TOURNAMENTS);
    }

    @Override
    public String getCurrentRecords() {
        return describeTournaments(tournament -> {
            Standings standings = computeStandings(tournament, new PlayerSet());
            return RecordCount.print(standings.getRecordCounts(3));
        }, true, true, NO_ACTIVE_TOURNAMENTS);
    }

    @Override
    public String getCurrentStatus(final String name) {
        return describeTournaments(tournament -> {
            PlayerSet players = new PlayerSet();
            Standings standings = computeStandings(tournament, players);
            Player player = players.findByName(name);
            if (player == null) {
                return null;
            }
            Record record = standings.getRecord(player);
            if (record.isDropped()) {
                return String.format("%s dropped.");
            } else {
                int round = getCurrentRound(tournament);
                PairingsJson pairingsJson = client.getPairings(tournament.getPairingsIdMap().get(round), 500);
                Pairings pairings = computePairings(tournament, players, round, pairingsJson);

                if (pairings.isDone()) {
                    return String.format("%s is %s. Round %d has ended.", player.getName(), record, round);
                } else if (pairings.hasResult(player)) {
                    int matchesLeft = pairings.getMatchesLeft();
                    if (matchesLeft == 1) {
                        return String.format("%s is %s. There is %d match left in round %d", player.getName(), record,
                                matchesLeft, round);
                    }
                    return String.format("%s is %s. There are %d matches left in round %d", player.getName(), record,
                        matchesLeft, round);
                }

                String status = String.format("%s is %s and ", player.getName(), record);
                Player opponent = pairings.getOpponent(player);
                if (pairings.getOpponent(player) == Player.BYE) {
                    return status + String.format("has a bye.", player.getName());
                } else {
                    Map<Player, DecklistDescription> decklistMap = computeDecklistMap(players, pairingsJson);
                    DecklistDescription opponentDeck = decklistMap.get(opponent);
                    return status + String.format("playing against %s on %s, %s", opponent.getName(),
                            opponentDeck.getName(), opponentDeck.getUrl());
                }
            }
        }, true, false, String.format("There are no current tournaments for %s.", name));
    }

    @Override
    public String getCurrentRecord(final String name) {
        return describeTournaments(tournament -> {
            PlayerSet players = new PlayerSet();
            Standings standings = computeStandings(tournament, players);
            Player player = players.findByName(name);
            if (player == null) {
                return null;
            }
            return standings.getRecord(player).toString();
        }, true, true, String.format("There are no current tournaments for %s.", name));
    }

    public String getCurrentDecklist(final String name) {
        return describeTournaments(tournament -> {
            PlayerSet players = new PlayerSet();
            PairingsJson pairingsJson = client.getPairings(tournament.getPairingsIdMap().get(1), 500);
            Map<Player, DecklistDescription> decklistMap = computeDecklistMap(players, pairingsJson);
            Player player = players.findByName(name);
            if (player == null) {
                return null;
            }

            DecklistDescription description = decklistMap.get(player);
            return String.format("%s (%s)", description.getUrl(), description.getName());
        }, true, false, String.format("%s is not in the tournament.", name));
    }

    @Override
    public Tournament getTournament(int tournamentId) {
        return getTournament(parser.parse(tournamentId));
    }

    public static TournamentType getType(final String name) {
        if (name.startsWith("Players Tour Finals")) {
            return TournamentType.PLAYERS_TOUR_FINALS;
        }

        if (name.startsWith("SCG Tour Online - Standard Challenge")) {
            return TournamentType.DAILY;
        }

        if (name.startsWith("SCG Tour Online Championship Qualifier")) {
            return TournamentType.WEEKLY;
        }

        return TournamentType.ONE_OFF;
    }

    private String describeTournaments(final Function<MtgMeleeTournament, String> function, final boolean showHeader,
            final boolean showPunctuation, final String emptyTournamentMessage) {
        return Informer.describeTournaments(getCurrentTournaments(false), MtgMeleeInformer::getNickName,
                function, showHeader, showPunctuation, emptyTournamentMessage);
    }

    private Tournament getTournament(final MtgMeleeTournament tournament) {
        Tournament result = new Tournament(this, tournament.getName(), tournament.getId());
        result.setRound(getCurrentRound(tournament));
        result.setPairingsUrl(getPairingsUrl(tournament));
        result.setNickName(getNickName(tournament));
        result.setUrl(String.format("/tournament/melee/%d", tournament.getId()));
        result.setStandingsUrl(getStandingsUrl(tournament));
        result.setDecklistUrl(getDecklistsUrl(tournament));
        result.setStartTime(tournament.getStartTime());
        result.setType(tournament.getTournamentType());

        PlayerSet playerSet = new PlayerSet();
        result.setPlayerSet(playerSet);
        tournament.getPairingsIdMap()
                .forEach((round, pairingsId) -> result.setPairings(computePairings(tournament, playerSet, round)));

        if (tournament.getStandingsId() != -1) {
            StandingsJson standingsJson = client.getStandings(tournament.getStandingsId(), 500);
            Standings standings = computeStandings(tournament, playerSet, standingsJson, result);
            result.setStandings(standings);
            result.setDecklistMap(computeDecklistMap(playerSet, standingsJson));
        } else {
            result.setDecklistMap(new HashMap<>());
        }

        return result;
    }

    private Map<Player, DecklistDescription> computeDecklistMap(final PlayerSet playerSet,
            final StandingsJson standingsJson) {
        Map<Player, DecklistDescription> decklistMap = new HashMap<>();
        for (PlayerInfo playerInfo : standingsJson.getData()) {
            Player player = Player.createFromName(playerInfo.getName(), playerInfo.getTwitchChannel());
            player = playerSet.merge(player);
            decklistMap.put(player, new DecklistDescription(playerInfo.getDecklistName(),
                    getDecklistUrl(playerInfo.getDecklistId())));
        }
        return decklistMap;
    }

    private Map<Player, DecklistDescription> computeDecklistMap(final PlayerSet players,
            final PairingsJson pairingsJson) {
        Map<Player, DecklistDescription> decklistMap = new HashMap<>();
        for (PairingInfo pairing : pairingsJson.getData()) {
            Player player = createPlayer(players, pairing.getPlayer1Name(), pairing.getPlayer1ArenaName(),
                    pairing.getPlayer1Discord(), pairing.getPlayer1Twitch());
            player = players.merge(player);
            decklistMap.put(player, new DecklistDescription(pairing.getPlayer1DecklistName(),
                    getDecklistUrl(pairing.getPlayer1DecklistId())));
            Player opponent = createPlayer(players, pairing.getPlayer2Name(), pairing.getPlayer2ArenaName(),
                    pairing.getPlayer2Discord(), pairing.getPlayer2Twitch());
            opponent = players.merge(opponent);
            if (opponent != Player.BYE) {
                decklistMap.put(opponent, new DecklistDescription(pairing.getPlayer2DecklistName(),
                        getDecklistUrl(pairing.getPlayer2DecklistId())));
            }
        }
        return decklistMap;
    }

    private List<MtgMeleeTournament> getCurrentTournaments(final boolean includeEarly) {
        TournamentsJson tournamentsJson = client.getTournaments("Star City Games", 500);

        List<TournamentJson> filteredTournamentJsons = new ArrayList<>();
        Instant now = Instant.now();
        TournamentType minType = addTournaments(tournamentsJson, now, filteredTournamentJsons);
        tournamentsJson = client.getTournaments("The Bash Bros Podcast", 500);

        TournamentType tournamentType = addTournaments(tournamentsJson, now, filteredTournamentJsons);
        if (tournamentType.compareTo(minType) < 0) {
            minType = tournamentType;
        }

        final TournamentType filterType = minType;
        return filteredTournamentJsons.stream()
                .filter(tournamentJson -> getType(tournamentJson.getName()) == filterType)
                .map(tournamentJson -> parser.parse(tournamentJson.getId())).collect(Collectors.toList());
    }

    private TournamentType addTournaments(final TournamentsJson tournamentsJson, final Instant now,
            final List<TournamentJson> filteredTournamentJsons) {
        TournamentType minType = TournamentType.NONE;
        for (TournamentJson tournamentJson : tournamentsJson.getData()) {
            if (isRecent(tournamentJson, now)) {
                filteredTournamentJsons.add(tournamentJson);
                TournamentType tournamentType = getType(tournamentJson.getName());
                if (tournamentType.compareTo(minType) < 0) {
                    minType = tournamentType;
                }
            }
        }
        return minType;
    }

    private boolean isRecent(final TournamentJson tournamentJson, final Instant now) {
        Instant startTime = LocalDateTime.parse(tournamentJson.getStartTime(),
                DateTimeFormatter.ISO_LOCAL_DATE_TIME).toInstant(ZoneOffset.UTC);
        int length = getLengthInHours(getType(tournamentJson.getName()));
        return Duration.between(startTime, now).toHours() < length;
    }

    private static int getLengthInHours(final TournamentType tournamentType) {
        switch (tournamentType) {
            case DAILY:
                return 4;
            case WEEKLY:
                return 9;
        }
        return 6;
    }

    private Standings computeStandings(final MtgMeleeTournament tournament, final PlayerSet playerSet) {
        StandingsJson standingsJson = client.getStandings(tournament.getStandingsId(), 500);
        return computeStandings(tournament, playerSet, standingsJson, null);
    }

    private int computeRound(final StandingsJson standingsJson) {
        int maxRound = 0;
        for (PlayerInfo playerInfo : standingsJson.getData()) {
            int playerRounds = playerInfo.getWins() + playerInfo.getLosses() + playerInfo.getDraws();
            maxRound = Math.max(maxRound, playerRounds);
        }
        return maxRound;
    }

    private Standings computeStandings(final MtgMeleeTournament tournament, final PlayerSet playerSet,
            final StandingsJson standingsJson, final Tournament fullTournament) {
        Standings standings = new Standings(playerSet, computeRound(standingsJson));

        Pairings pairings;
        if (fullTournament == null) {
            pairings = computePairings(tournament, playerSet, getCurrentRound(tournament));
        } else {
            pairings = fullTournament.getMostRecentPairings();
        }

        for (PlayerInfo playerInfo : standingsJson.getData()) {
            Player player = Player.createFromName(playerInfo.getName(), playerInfo.getTwitchChannel());

            player = playerSet.merge(player);
            boolean dropped = pairings.hasDropped(player);
            Record record =
                    Record.newRecord(playerInfo.getWins(), playerInfo.getLosses(), playerInfo.getDraws(), dropped);

            if (!pairings.isDone() && pairings.hasResult(player)) {
                switch (pairings.getResult(player)) {
                    case WIN:
                        record = record.addWin();
                        break;
                    case DRAW:
                        record = record.addDraw();
                        break;
                    case LOSS:
                        record = record.addLoss();
                        break;
                    case NONE:
                }
            }

            standings.add(player, record);
            standings.setRank(player, playerInfo.getRank());
        }

        return standings;
    }

    private PlayerStanding.Result parseResult(final PairingInfo pairing) {
        if (!pairing.isHasResults()) {
            return PlayerStanding.Result.NONE;
        }

        String resultString = pairing.getResult();

        if (resultString.endsWith("was awarded a bye")) {
            return PlayerStanding.Result.WIN;
        }

        if (resultString.endsWith(" Draw")) {
            return PlayerStanding.Result.DRAW;
        }

        if (resultString.startsWith(pairing.getPlayer1Name())) {
            return PlayerStanding.Result.WIN;
        }

        return PlayerStanding.Result.LOSS;
    }

    private Pairings computePairings(final MtgMeleeTournament tournament, final PlayerSet players, final int round) {
        PairingsJson pairingsJson = client.getPairings(tournament.getPairingsIdMap().get(round), 500);
        return computePairings(tournament, players, round, pairingsJson);
    }

    private Pairings computePairings(final MtgMeleeTournament tournament, final PlayerSet players, final int round,
            final PairingsJson pairingsJson) {
        Pairings pairings = new Pairings(players, round, null);

        for (PairingInfo pairing : pairingsJson.getData()) {
            Player player = createPlayer(players, pairing.getPlayer1Name(), pairing.getPlayer1ArenaName(),
                    pairing.getPlayer1Discord(), pairing.getPlayer1Twitch());
            Player opponent = createPlayer(players, pairing.getPlayer2Name(), pairing.getPlayer2ArenaName(),
                    pairing.getPlayer2Discord(), pairing.getPlayer2Twitch());

            PlayerStanding.Result result = parseResult(pairing);
            pairings.add(player, opponent, result);
            if (opponent != Player.BYE) {
                pairings.add(opponent, player, PlayerStanding.Result.reverse(result));
            }
        }

        return pairings;
    }

    private int getCurrentRound(final MtgMeleeTournament tournament) {
        if (tournament.getPairingsIdMap().isEmpty()) {
            return 0;
        }
        return Collections.max(tournament.getPairingsIdMap().keySet());
    }

    private String getPairingsUrl(final MtgMeleeTournament tournament) {
        return String.format("https://mtgmelee.com/Tournament/View/%d#%s", tournament.getId(), PAIRINGS_ID);
    }

    private String getStandingsUrl(final MtgMeleeTournament tournament) {
        return String.format("https://mtgmelee.com/Tournament/View/%d#%s", tournament.getId(), STANDINGS_ID);
    }

    private static String getNickName(final MtgMeleeTournament tournament) {
        if (tournament.getTournamentType() == TournamentType.DAILY) {
            ZonedDateTime startTime = tournament.getStartTime().atZone(ZoneId.of("America/New_York"));
            return String.format("SCG Challenge (%s)", DateTimeFormatter.ofPattern("h:mm").format(startTime.toLocalTime()));
        }

        if (tournament.getTournamentType() == TournamentType.WEEKLY) {
            int numIndex = tournament.getName().indexOf("#") + 1;
            int qualifierNum = Integer.parseInt(tournament.getName().substring(numIndex));
            return String.format("SCG Qualifier #%d", qualifierNum);
        }

        if (tournament.getTournamentType() == TournamentType.ONE_OFF) {
            if (tournament.getName().contains("Bash Bros Battles")) {
                return "Bash Bros Battles";
            }
        }

        return tournament.getName();
    }

    private String getDecklistsUrl(final MtgMeleeTournament tournament) {
        return String.format("https://mtgmelee.com/Tournament/View/%d", tournament.getId());
    }

    private String getDecklistUrl(final int decklistId) {
        return String.format("https://mtgmelee.com/Decklist/View/%d", decklistId);
    }

    private Player createPlayer(final PlayerSet players, final String name, final String arenaName,
            final String discordName, final String twitchName) {
        if (name == null && arenaName == null) {
            return Player.BYE;
        }
        Player player = new Player(arenaName, discordName, name, null, twitchName, null);
        return players.merge(player);
    }
}
