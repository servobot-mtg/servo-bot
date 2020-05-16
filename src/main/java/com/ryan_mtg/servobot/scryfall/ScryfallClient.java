package com.ryan_mtg.servobot.scryfall;

import feign.Feign;
import feign.Logger;
import feign.QueryMap;
import feign.RequestLine;
import feign.codec.Decoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.okhttp.OkHttpClient;
import feign.slf4j.Slf4jLogger;

import java.util.Map;

public interface ScryfallClient {
    @RequestLine("GET /cards/search")
    CardList cardSearch(@QueryMap Map<String, String> queryMap);

    @RequestLine("GET /cards/named")
    Card fuzzySearchForCardByName(@QueryMap Map<String, String> fuzzy);

    static String getServerUrl() {
        return "https://api.scryfall.com";
    }

    static ScryfallClient newClient() {
        Decoder decoder = new JacksonDecoder();
        return Feign.builder().client(new OkHttpClient()).encoder(new JacksonEncoder()).decoder(decoder)
                .errorDecoder(new ScryfallErrorDecoder(decoder))
                .logger(new Slf4jLogger(ScryfallClient.class)).logLevel(Logger.Level.FULL)
                .target(ScryfallClient.class, getServerUrl());
    }
}
