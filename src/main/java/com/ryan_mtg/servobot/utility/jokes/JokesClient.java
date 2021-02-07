package com.ryan_mtg.servobot.utility.jokes;

import feign.Feign;
import feign.Headers;
import feign.Logger;
import feign.RequestLine;
import feign.codec.StringDecoder;
import feign.okhttp.OkHttpClient;
import feign.slf4j.Slf4jLogger;

public interface JokesClient {
    @RequestLine("GET /")
    @Headers({"Accept: text/plain", "User-Agent: ServoBot"})
    String getJoke();

    static JokesClient newClient() {
        String url = getServerUrl();
        return Feign.builder().client(new OkHttpClient()).decoder(new StringDecoder())
                .logger(new Slf4jLogger(JokesClient.class)).logLevel(Logger.Level.FULL).target(JokesClient.class, url);
    }

    static String getServerUrl() {
        return "https://icanhazdadjoke.com";
    }
}