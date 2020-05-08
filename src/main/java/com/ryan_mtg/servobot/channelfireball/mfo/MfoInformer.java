package com.ryan_mtg.servobot.channelfireball.mfo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Slf4j
@Component
public class MfoInformer {
    private MfoClient mfoClient;
    private Clock clock;

    public MfoInformer() {
        this(MfoClient.newClient(), Clock.systemUTC());
    }

    public MfoInformer(final MfoClient mfoClient, final Clock clock) {
        this.mfoClient = mfoClient;
        this.clock = clock;
    }

    public List<Tournament> getCurrentTournaments() {
        TournamentSeriesList seriesList = mfoClient.getTournamentSeriesList();
        List<Tournament> tournaments = new ArrayList<>();
        for (TournamentSeries series : seriesList.getData()) {
            ZoneId zoneId = ZoneId.of(series.getTimezone());
            Instant startTime = parse(series.getStartDate(), zoneId);
            Instant endTime = parse(series.getEndDate(), zoneId);
            Instant now = clock.instant();
            if (startTime.compareTo(now) < 0 &&
                    (now.compareTo(endTime) < 0 || series.getName().contains("MagicFest Online"))) {
                tournaments.addAll(getCurrentTournaments(zoneId, series.getId()));
            }
        }
        return tournaments;
    }

    public String describeCurrentTournaments() {
        return describeTournaments(tournament -> tournament.getName(), true);
    }

    public String getCurrentDecklists() {
        return describeTournaments(
                tournament -> resolve(String.format("/deck/%d", tournament.getId())), false);
    }

    public String getCurrentPairings() {
        return describeTournaments(
                tournament -> resolve(String.format("/pairings/%d", tournament.getId())), false);
    }

    public String getCurrentStandings() {
        return describeTournaments(
                tournament -> resolve(String.format("/standings/%d", tournament.getId())), false);
    }

    public String getCurrentRound() {
        return describeTournaments(
                tournament -> String.format("round %d", tournament.getCurrentRound()), false);
    }

    private List<Tournament> getCurrentTournaments(final ZoneId zoneId, final int tournamentSeriesId) {
        TournamentList tournamentList = mfoClient.getTournamentList(tournamentSeriesId);
        List<Tournament> tournaments = new ArrayList<>();
        for (Tournament tournament : tournamentList.getData()) {
            Instant startTime = parse(tournament.getStartsAt(), zoneId);
            Instant lastUpdatedTime = parse(tournament.getLastUpdated());
            Instant now = clock.instant();
            if (startTime.compareTo(now) < 0
                    && now.compareTo(lastUpdatedTime.plus(2, ChronoUnit.HOURS)) < 0) {
                tournaments.add(tournament);
            }
        }
        return tournaments;
    }

    private Instant parse(final String time) {
        return LocalDateTime.parse(time, DateTimeFormatter.ISO_OFFSET_DATE_TIME).toInstant(ZoneOffset.UTC);
    }

    private Instant parse(final String time, final ZoneId zoneId) {
        return LocalDateTime.parse(time, DateTimeFormatter.ISO_OFFSET_DATE_TIME).atZone(zoneId).toInstant();
    }

    public String resolve(final String path) {
        return MfoClient.getServerUrl() + path;
    }

    private String describeTournaments(final Function<Tournament, String> function, final boolean punctuation) {
        List<Tournament> tournaments = getCurrentTournaments();
        if (tournaments.size() > 1) {
            StringBuilder builder = new StringBuilder();
            int seen = 0;
            for (Tournament tournament : tournaments) {
                seen++;
                if (!punctuation) {
                    builder.append(tournament.getName()).append(": ");
                }
                builder.append(function.apply(tournament));
                if (seen + 1 == tournaments.size()) {
                    builder.append(" and ");
                } else if (seen == tournaments.size()) {
                    if (punctuation) {
                        builder.append('.');
                    }
                } else {
                    if (punctuation) {
                        builder.append(", ");
                    } else {
                        builder.append(" ");
                    }
                }
            }
            return builder.toString();
        } else if (tournaments.size() == 1) {
            return function.apply(tournaments.get(0));
        } else {
            return "There are no active tournaments.";
        }
    }
}
