package com.ryan_mtg.servobot.twitch.twitchemotes;

import com.ryan_mtg.servobot.twitch.twitchemotes.json.TwitchEmotes;
import feign.Feign;
import feign.Logger;
import feign.Param;
import feign.RequestLine;
import feign.jackson.JacksonDecoder;
import feign.okhttp.OkHttpClient;
import feign.slf4j.Slf4jLogger;

public interface TwitchEmotesClient {
    @RequestLine("GET /api/v4/channels/{channelId}")
    TwitchEmotes getChannelEmotes(@Param("channelId") final long channelId);

    static TwitchEmotesClient newClient() {
        String url = getServerUrl();
        return Feign.builder().client(new OkHttpClient()).decoder(new JacksonDecoder())
                .logger(new Slf4jLogger(TwitchEmotesClient.class)).logLevel(Logger.Level.FULL)
                .target(TwitchEmotesClient.class, url);
    }

    static String getServerUrl() {
        return "https://api.twitchemotes.com";
    }
}
