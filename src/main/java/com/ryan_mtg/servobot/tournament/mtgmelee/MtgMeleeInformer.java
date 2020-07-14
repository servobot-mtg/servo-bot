package com.ryan_mtg.servobot.tournament.mtgmelee;

import com.ryan_mtg.servobot.tournament.DecklistDescription;
import com.ryan_mtg.servobot.tournament.Informer;
import com.ryan_mtg.servobot.tournament.Pairings;
import com.ryan_mtg.servobot.tournament.Player;
import com.ryan_mtg.servobot.tournament.PlayerSet;
import com.ryan_mtg.servobot.tournament.Record;
import com.ryan_mtg.servobot.tournament.RecordCount;
import com.ryan_mtg.servobot.tournament.Standings;
import com.ryan_mtg.servobot.tournament.Tournament;
import com.ryan_mtg.servobot.tournament.TournamentType;
import com.ryan_mtg.servobot.tournament.channelfireball.mfo.json.Pairing;
import com.ryan_mtg.servobot.tournament.channelfireball.mfo.json.PlayerStanding;
import com.ryan_mtg.servobot.tournament.mtgmelee.json.PairingInfo;
import com.ryan_mtg.servobot.tournament.mtgmelee.json.PairingsJson;
import com.ryan_mtg.servobot.tournament.mtgmelee.json.PlayerInfo;
import com.ryan_mtg.servobot.tournament.mtgmelee.json.StandingsJson;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MtgMeleeInformer implements Informer {
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
    public String describeCurrentTournaments() {
        return String.format("%s.", getCurrentTournaments().get(0).getName());
    }

    @Override
    public String getCurrentDecklists() {
        MtgMeleeTournament tournament = getCurrentTournaments().get(0);
        return String.format("%s: %s", tournament.getName(), getDecklistsUrl(tournament));
    }

    @Override
    public String getCurrentPairings() {
        MtgMeleeTournament tournament = getCurrentTournaments().get(0);
        return String.format("%s: %s", tournament.getName(), getPairingsUrl(tournament));
    }

    @Override
    public String getCurrentStandings() {
        MtgMeleeTournament tournament = getCurrentTournaments().get(0);
        return String.format("%s: %s", tournament.getName(), getStandingsUrl(tournament));
    }

    @Override
    public String getCurrentRound() {
        MtgMeleeTournament tournament = getCurrentTournaments().get(0);
        return String.format("%s: round %d", tournament.getName(), getCurrentRound(tournament));
    }

    @Override
    public String getCurrentRecords() {
        MtgMeleeTournament tournament = getCurrentTournaments().get(0);
        Standings standings = computeStandings(tournament, new PlayerSet());
        return RecordCount.print(standings.getRecordCounts(3));
    }

    @Override
    public Tournament getTournament(int tournamentId) {
        return getTournament(parser.parse(tournamentId));
    }

    private Tournament getTournament(MtgMeleeTournament tournament) {
        Tournament result = new Tournament(this, tournament.getName(), tournament.getId());
        result.setRound(getCurrentRound(tournament));
        result.setPairingsUrl(getPairingsUrl(tournament));
        result.setNickName(getNickName(tournament));
        result.setStandingsUrl(getStandingsUrl(tournament));
        result.setDecklistUrl(getDecklistsUrl(tournament));
        result.setStartTime(null); //parse(tournament.getStartsAt(), zoneId));
        result.setType(TournamentType.ONE_OFF); //TO DO, figure out a way to describe the type of tournament

        PlayerSet playerSet = new PlayerSet();
        tournament.getPairingsIdMap()
                .forEach((round, pairingsId) -> result.setPairings(computePairings(tournament, playerSet, round)));

        StandingsJson standingsJson = client.getStandings(tournament.getStandingsId(), 500);
        Standings standings = computeStandings(tournament, playerSet, standingsJson, result);
        result.setStandings(standings);

        result.setDecklistMap(computeDecklistMap(playerSet, standingsJson));

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

    private List<MtgMeleeTournament> getCurrentTournaments() {
        return Collections.singletonList(parser.parse(2166));
    }

    private Standings computeStandings(final MtgMeleeTournament tournament, final PlayerSet playerSet) {
        StandingsJson standingsJson = client.getStandings(tournament.getStandingsId(), 500);
        return computeStandings(tournament, playerSet, standingsJson, null);
    }

    private Standings computeStandings(final MtgMeleeTournament tournament, final PlayerSet playerSet,
            final StandingsJson standingsJson, final Tournament fullTournament) {
        int round = getCurrentRound(tournament);

        if (round == 4) { //max round
            if (fullTournament != null && fullTournament.getMostRecentPairings().getRound() == round) {

            } else {
                int maxPoints = 0;
                for (PlayerInfo playerInfo : standingsJson.getData()) {
                    maxPoints = Math.max(maxPoints, playerInfo.getPoints());
                }

                if (maxPoints < 3 * round) {
                    round--;
                }
            }
        } else {
            round--;
        }

        Standings standings = new Standings(playerSet, round);

        for (PlayerInfo playerInfo : standingsJson.getData()) {
            Player player = Player.createFromName(playerInfo.getName(), playerInfo.getTwitchChannel());
            player = playerSet.merge(player);
            standings.add(player, Record.newRecord(playerInfo.getPoints(), round));
            standings.setRank(player, playerInfo.getRank());
        }

        return standings;
    }

    private Pairings computePairings(final MtgMeleeTournament tournament, final PlayerSet players, final int round) {
        //Instant roundStartTime = parse(tournament.getPairingsLastUpdated());
        PairingsJson pairingsJson = client.getPairings(tournament.getPairingsIdMap().get(round), 500);
        Pairings pairings = new Pairings(players, round, null);

        for (PairingInfo pairing : pairingsJson.getData()) {
            Player player = createPlayer(players, pairing.getPlayer1Name(), pairing.getPlayer1ArenaName(),
                    pairing.getPlayer1Discord(), pairing.getPlayer1Twitch());
            Player opponent = createPlayer(players, pairing.getPlayer2Name(), pairing.getPlayer2ArenaName(),
                    pairing.getPlayer2Discord(), pairing.getPlayer2Twitch());
            pairings.add(player, opponent);
            pairings.add(opponent, player);
        }

        return pairings;
    }

    private int getCurrentRound(final MtgMeleeTournament tournament) {
        return Collections.max(tournament.getPairingsIdMap().keySet());
    }

    private String getPairingsUrl(final MtgMeleeTournament tournament) {
        return String.format("https://mtgmelee.com/Tournament/View/%d#%s", tournament.getId(),
                MtgMeleeWebParser.PAIRINGS_ID);
    }

    private String getStandingsUrl(final MtgMeleeTournament tournament) {
        return String.format("https://mtgmelee.com/Tournament/View/%d#%s", tournament.getId(),
                MtgMeleeWebParser.STANDINGS_ID);
    }

    private String getNickName(final MtgMeleeTournament tournament) {
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
        Player player = new Player(arenaName, discordName, name, null, twitchName, null);
        return players.merge(player);
    }
}
