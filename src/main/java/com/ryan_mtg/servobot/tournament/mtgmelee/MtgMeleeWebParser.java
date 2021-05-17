package com.ryan_mtg.servobot.tournament.mtgmelee;

import com.ryan_mtg.servobot.tournament.TournamentType;
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
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class MtgMeleeWebParser {
    private static final String STANDINGS_ID = "standings-phase-selector-container";
    private static final String PAIRINGS_ID = "pairings-round-selector-container";

    public MtgMeleeTournament parse(final int tournamentId) {
        try {
            String url = String.format("https://mtgmelee.com/Tournament/View/%d", tournamentId);
            HttpClient httpClient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(url);
            HttpResponse response = httpClient.execute(httpGet);

            Document document = Jsoup.parse(response.getEntity().getContent(), StandardCharsets.UTF_8.name(), url);

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

            Element headline = document.getElementById("tournament-headline-registration");
            String headlineText = headline.text();
            int formatIndex = headlineText.indexOf("Format:");
            int endIndex = headlineText.indexOf('|', formatIndex);
            tournament.setFormat(headlineText.substring(formatIndex + 8, endIndex).trim());

            tournament.setTournamentType(getTournamentType(tournament.getName()));

            return tournament;
        } catch (IOException e) {
            return null;
        }
    }

    private TournamentType getTournamentType(final String name) {
        return MtgMeleeInformer.guessType(name);
    }

    private int getStandingsId(final Document document) {
        Element standingsDiv = document.getElementById(STANDINGS_ID);
        if (standingsDiv == null) {
            return -1;
        }
        Elements standingsButtons = standingsDiv.getElementsByTag("button");

        int standingsId = -1;
        for (Element button : standingsButtons) {
            Map<String, String> data = button.dataset();
            if (data != null && data.get("is-started").equals("True")) {
                String name = button.text();
                if (name.contains("Swiss") || name.contains("Day") || name.contains("Round")) {
                    standingsId = Integer.parseInt(data.get("id"));
                }
            }
        }
        return standingsId;
    }

    private Map<Integer, Integer> getPairingsMap(final Document document) {
        Map<Integer, Integer> pairingsMap = new HashMap<>();

        Element pairingsDiv = document.getElementById(PAIRINGS_ID);
        if (pairingsDiv == null) {
            return pairingsMap;
        }
        Elements pairingsButtons = pairingsDiv.getElementsByTag("button");

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
