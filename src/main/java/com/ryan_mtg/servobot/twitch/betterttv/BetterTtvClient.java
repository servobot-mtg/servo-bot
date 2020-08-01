package com.ryan_mtg.servobot.twitch.betterttv;

import com.ryan_mtg.servobot.twitch.betterttv.json.EmotesJson;
import feign.Feign;
import feign.Logger;
import feign.Param;
import feign.RequestLine;
import feign.jackson.JacksonDecoder;
import feign.okhttp.OkHttpClient;
import feign.slf4j.Slf4jLogger;

public interface BetterTtvClient {
    @RequestLine("GET /3/cached/users/twitch/{channelId}")
    EmotesJson getChannelEmotes(@Param("channelId") final long channelId);

    static BetterTtvClient newClient() {
        String url = getServerUrl();
        return Feign.builder().client(new OkHttpClient()).decoder(new JacksonDecoder())
                .logger(new Slf4jLogger(BetterTtvClient.class)).logLevel(Logger.Level.FULL)
                .target(BetterTtvClient.class, url);
    }

    static String getServerUrl() {
        return "https://api.betterttv.net";
    }
}
