package com.ryan_mtg.servobot.channelfireball.mfo;

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

    static String getServerUrl() {
        return "https://my.cfbevents.com";
    }

    static MfoClient newClient() {
        return Feign.builder().client(new OkHttpClient()).encoder(new JacksonEncoder()).decoder(new JacksonDecoder())
                .logger(new Slf4jLogger(MfoClient.class)).logLevel(Logger.Level.FULL)
                .target(MfoClient.class, getServerUrl());
    }
}
