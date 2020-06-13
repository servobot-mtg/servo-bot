package com.ryan_mtg.servobot.channelfireball.mfo;

import com.ryan_mtg.servobot.channelfireball.mfo.json.PairingsJson;
import com.ryan_mtg.servobot.channelfireball.mfo.json.Standings;
import com.ryan_mtg.servobot.channelfireball.mfo.json.TournamentList;
import com.ryan_mtg.servobot.channelfireball.mfo.json.TournamentSeriesList;
import feign.Feign;
import feign.Logger;
import feign.Param;
import feign.RequestLine;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.okhttp.OkHttpClient;
import feign.slf4j.Slf4jLogger;

public interface MfoClient {
    @RequestLine("GET /api/json")
    TournamentSeriesList getTournamentSeriesList();

    @RequestLine("GET /api/json/tournaments/{seriesId}")
    TournamentList getTournamentList(@Param("seriesId") int seriesId);

    @RequestLine("GET /api/json/pairings/{tournamentId}")
    PairingsJson getPairings(@Param("tournamentId") int tournamentId);

    @RequestLine("GET /api/json/standings/{tournamentId}")
    Standings getStandings(@Param("tournamentId") int tournamentId);

    static String getServerUrl() {
        return "https://my.cfbevents.com";
    }

    static MfoClient newClient() {
        return Feign.builder().client(new OkHttpClient()).encoder(new JacksonEncoder()).decoder(new JacksonDecoder())
                .logger(new Slf4jLogger(MfoClient.class)).logLevel(Logger.Level.FULL)
                .target(MfoClient.class, getServerUrl());
    }
}
