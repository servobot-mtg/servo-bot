package com.ryan_mtg.servobot.tournament.mtgmelee;

import org.apache.commons.codec.Charsets;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class MtgMeleeWebParser {
    public static final String STANDINGS_ID = "standings-phase-selector-container";
    public static final String PAIRINGS_ID = "pairings-round-selector-container";

    public MtgMeleeTournament parse(final int tournamentId) {
        try {
            String url = String.format("https://mtgmelee.com/Tournament/View/%d", tournamentId);
            HttpClient httpClient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(url);
            HttpResponse response = httpClient.execute(httpGet);

            Document document = Jsoup.parse(response.getEntity().getContent(), Charsets.UTF_8.name(), url);

            MtgMeleeTournament tournament = new MtgMeleeTournament();
            tournament.setId(tournamentId);

            Element nameElement = document.select("meta[property=og:title]").get(0);
            tournament.setName(nameElement.attr("content"));

            Element startDateP = document.getElementById("tournament-headline-start-date-field");
            String startTimeString = startDateP.getElementsByTag("span").get(0).dataset().get("value");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy h:mm:ss a");

            LocalDateTime startDate = LocalDateTime.parse(startTimeString, formatter);
            Instant startTime = startDate.toInstant(ZoneOffset.UTC);

            tournament.setPairingsIdMap(getPairingsMap(document));
            tournament.setStandingsId(getStandingsId(document));
            tournament.setStartTime(startTime);

            return tournament;
        } catch (IOException e) {
            return null;
        }
    }

    private int getStandingsId(final Document document) {
        Element standingsDiv = document.getElementById(STANDINGS_ID);
        Elements standingsButtons = standingsDiv.getElementsByTag("button");

        for (Element button : standingsButtons) {
            Map<String, String> data = button.dataset();
            if (data != null && data.get("is-started").equals("True")) {
                String name = data.get("name");
                if (button.text().equals("Swiss")) {
                    return Integer.parseInt(data.get("id"));
                }
            }
        }
        return -1;
    }

    private Map<Integer, Integer> getPairingsMap(final Document document) {
        Element pairingsDiv = document.getElementById(PAIRINGS_ID);
        Elements pairingsButtons = pairingsDiv.getElementsByTag("button");

        Map<Integer, Integer> pairingsMap = new HashMap<>();
        for (Element button : pairingsButtons) {
            Map<String, String> data = button.dataset();
            if (data != null && data.get("is-started").equals("True")) {
                String name = data.get("name");
                if (name.startsWith("Round ")) {
                    int round = Integer.parseInt(name.substring(6));
                    int id = Integer.parseInt(data.get("id"));
                    pairingsMap.put(round, id);
                }
            }
        }
        return pairingsMap;
    }
}
