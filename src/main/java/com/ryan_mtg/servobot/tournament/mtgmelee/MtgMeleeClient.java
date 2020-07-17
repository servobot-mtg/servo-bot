package com.ryan_mtg.servobot.tournament.mtgmelee;

import com.ryan_mtg.servobot.tournament.mtgmelee.json.FeatureTournamentJson;
import com.ryan_mtg.servobot.tournament.mtgmelee.json.PairingsJson;
import com.ryan_mtg.servobot.tournament.mtgmelee.json.StandingsJson;
import com.ryan_mtg.servobot.tournament.mtgmelee.json.TournamentJson;
import com.ryan_mtg.servobot.tournament.mtgmelee.json.TournamentsJson;
import feign.Body;
import feign.Feign;
import feign.Headers;
import feign.Logger;
import feign.Param;
import feign.RequestLine;
import feign.jackson.JacksonDecoder;
import feign.okhttp.OkHttpClient;
import feign.slf4j.Slf4jLogger;

import java.util.List;

public interface MtgMeleeClient {
    @RequestLine("POST /Tournament/GetPhaseStandings/{standingsId}")
    @Headers("Content-Type: application/x-www-form-urlencoded; charset=UTF-8")
    @Body("draw=5&columns%5B0%5D%5Bdata%5D=Rank&columns%5B0%5D%5Bname%5D=Rank&columns%5B0%5D%5Bsearchable%5D=false&columns%5B0%5D%5Borderable%5D=true&columns%5B0%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B0%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B1%5D%5Bdata%5D=Name&columns%5B1%5D%5Bname%5D=Name&columns%5B1%5D%5Bsearchable%5D=true&columns%5B1%5D%5Borderable%5D=true&columns%5B1%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B1%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B2%5D%5Bdata%5D=TwitchChannel&columns%5B2%5D%5Bname%5D=TwitchChannel&columns%5B2%5D%5Bsearchable%5D=true&columns%5B2%5D%5Borderable%5D=true&columns%5B2%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B2%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B3%5D%5Bdata%5D=DecklistName&columns%5B3%5D%5Bname%5D=DecklistName&columns%5B3%5D%5Bsearchable%5D=true&columns%5B3%5D%5Borderable%5D=true&columns%5B3%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B3%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B4%5D%5Bdata%5D=Points&columns%5B4%5D%5Bname%5D=Points&columns%5B4%5D%5Bsearchable%5D=false&columns%5B4%5D%5Borderable%5D=true&columns%5B4%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B4%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B5%5D%5Bdata%5D=Tiebreaker1&columns%5B5%5D%5Bname%5D=Tiebreaker1&columns%5B5%5D%5Bsearchable%5D=false&columns%5B5%5D%5Borderable%5D=true&columns%5B5%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B5%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B6%5D%5Bdata%5D=Tiebreaker2&columns%5B6%5D%5Bname%5D=Tiebreaker2&columns%5B6%5D%5Bsearchable%5D=false&columns%5B6%5D%5Borderable%5D=true&columns%5B6%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B6%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B7%5D%5Bdata%5D=Tiebreaker3&columns%5B7%5D%5Bname%5D=Tiebreaker3&columns%5B7%5D%5Bsearchable%5D=false&columns%5B7%5D%5Borderable%5D=true&columns%5B7%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B7%5D%5Bsearch%5D%5Bregex%5D=false&order%5B0%5D%5Bcolumn%5D=0&order%5B0%5D%5Bdir%5D=asc" +
            "&start=0&length={length}&search%5Bvalue%5D=&search%5Bregex%5D=false")
    StandingsJson getStandings(@Param("standingsId") int standingsId, @Param("length") int length);

    @RequestLine("POST /Tournament/GetRoundPairings/{pairingsId}")
    @Headers("Content-Type: application/x-www-form-urlencoded; charset=UTF-8")
    @Body("draw=3&columns%5B0%5D%5Bdata%5D=Player1&columns%5B0%5D%5Bname%5D=Player1&columns%5B0%5D%5Bsearchable%5D=true&columns%5B0%5D%5Borderable%5D=true&columns%5B0%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B0%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B1%5D%5Bdata%5D=Player1Twitch&columns%5B1%5D%5Bname%5D=Player1Twitch&columns%5B1%5D%5Bsearchable%5D=true&columns%5B1%5D%5Borderable%5D=true&columns%5B1%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B1%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B2%5D%5Bdata%5D=Player1Decklist&columns%5B2%5D%5Bname%5D=Player1Decklist&columns%5B2%5D%5Bsearchable%5D=true&columns%5B2%5D%5Borderable%5D=true&columns%5B2%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B2%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B3%5D%5Bdata%5D=Player2&columns%5B3%5D%5Bname%5D=Player2&columns%5B3%5D%5Bsearchable%5D=true&columns%5B3%5D%5Borderable%5D=true&columns%5B3%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B3%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B4%5D%5Bdata%5D=Player2Twitch&columns%5B4%5D%5Bname%5D=Player2Twitch&columns%5B4%5D%5Bsearchable%5D=true&columns%5B4%5D%5Borderable%5D=true&columns%5B4%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B4%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B5%5D%5Bdata%5D=Player2Decklist&columns%5B5%5D%5Bname%5D=Player2Decklist&columns%5B5%5D%5Bsearchable%5D=true&columns%5B5%5D%5Borderable%5D=true&columns%5B5%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B5%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B6%5D%5Bdata%5D=Result&columns%5B6%5D%5Bname%5D=Result&columns%5B6%5D%5Bsearchable%5D=false&columns%5B6%5D%5Borderable%5D=false&columns%5B6%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B6%5D%5Bsearch%5D%5Bregex%5D=false&order%5B0%5D%5Bcolumn%5D=0" +
            "&order%5B0%5D%5Bdir%5D=asc&start=0&length={length}&search%5Bvalue%5D=&search%5Bregex%5D=false")
    PairingsJson getPairings(@Param("pairingsId") int pairingsId, @Param("length") int length);

    @RequestLine("POST /Tournament/GetPastResults/")
    @Headers("Content-Type: application/x-www-form-urlencoded; charset=UTF-8")
    @Body("draw=2&columns%5B0%5D%5Bdata%5D=StartDate&columns%5B0%5D%5Bname%5D=StartDate&columns%5B0%5D%5Bsearchable%5D=false&columns%5B0%5D%5Borderable%5D=true&columns%5B0%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B0%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B1%5D%5Bdata%5D=Name&columns%5B1%5D%5Bname%5D=Name&columns%5B1%5D%5Bsearchable%5D=true&columns%5B1%5D%5Borderable%5D=true&columns%5B1%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B1%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B2%5D%5Bdata%5D=OrganizationName&columns%5B2%5D%5Bname%5D=OrganizationName&columns%5B2%5D%5Bsearchable%5D=true&columns%5B2%5D%5Borderable%5D=true&columns%5B2%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B2%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B3%5D%5Bdata%5D=Format&columns%5B3%5D%5Bname%5D=Format&columns%5B3%5D%5Bsearchable%5D=true&columns%5B3%5D%5Borderable%5D=true&columns%5B3%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B3%5D%5Bsearch%5D%5Bregex%5D=false&columns%5B4%5D%5Bdata%5D=Decklists&columns%5B4%5D%5Bname%5D=Decklists&columns%5B4%5D%5Bsearchable%5D=false&columns%5B4%5D%5Borderable%5D=true&columns%5B4%5D%5Bsearch%5D%5Bvalue%5D=&columns%5B4%5D%5Bsearch%5D%5Bregex%5D=false&order%5B0%5D%5Bcolumn%5D=4&order%5B0%5D%5Bdir%5D=desc" +
            "&start=0&length={length}&search%5Bvalue%5D={searchTerm}&search%5Bregex%5D=false")
    TournamentsJson getTournaments(@Param("searchTerm") String searchTerm, @Param("length") int length);

    @RequestLine("GET /Tournament/TodayTournaments")
    @Headers("Content-Type: application/x-www-form-urlencoded; charset=UTF-8")
    List<FeatureTournamentJson> getTodaysTournaments();

    static MtgMeleeClient newClient() {
        String url = getServerUrl();
        return Feign.builder().client(new OkHttpClient()).decoder(new JacksonDecoder())
                .logger(new Slf4jLogger(MtgMeleeClient.class)).logLevel(Logger.Level.FULL)
                .target(MtgMeleeClient.class, url);
    }

    static String getServerUrl() {
        return "https://mtgmelee.com";
    }
}