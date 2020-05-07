package com.ryan_mtg.servobot.channelfireball.mfo;

import lombok.extern.slf4j.Slf4j;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Slf4j
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
        List<Tournament> tournaments = getCurrentTournaments();
        if (tournaments.size() > 1) {
            StringBuilder builder = new StringBuilder();
            int seen = 0;
            for (Tournament tournament : tournaments) {
                seen++;
                builder.append(tournament.getName());
                if (seen + 1 == tournaments.size()) {
                    builder.append(" and ");
                } else if (seen == tournaments.size()) {
                    builder.append('.');
                } else {
                    builder.append(", ");
                }
            }
            return builder.toString();
        } else if (tournaments.size() == 1) {
            return tournaments.get(0).getName();
        } else {
            return "There are no active tournaments.";
        }
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
}
