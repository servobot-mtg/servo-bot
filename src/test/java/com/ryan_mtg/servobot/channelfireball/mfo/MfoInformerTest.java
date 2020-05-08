package com.ryan_mtg.servobot.channelfireball.mfo;

import org.junit.Before;
import org.junit.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MfoInformerTest {
    private static final String CURRENT_TIME = "2020-01-11T00:03:14+00:00";
    private static final String LOCAL_TIME_ZONE = "America/Los_Angeles";

    private MfoClient mockMfoClient;
    private Clock mockClock;

    private MfoInformer informer;

    List<Tournament> tournamentList;
    private Tournament tournament1;
    private Tournament tournament2;

    @Before
    public void setUp() {
        mockMfoClient = mock(MfoClient.class);
        mockClock = mock(Clock.class);
        informer = new MfoInformer(mockMfoClient, mockClock);
        when(mockClock.instant()).thenReturn(convertToInstant(CURRENT_TIME, LOCAL_TIME_ZONE));
        tournamentList = new ArrayList<>();

        TournamentSeries tournamentSeries = new TournamentSeries();
        tournamentSeries.setId(1);
        tournamentSeries.setName("Fake MagicFest Online Season X");
        tournamentSeries.setStartDate("2020-01-01T00:00:00+00:00");
        tournamentSeries.setEndDate("2020-02-01T00:00:00+00:00");
        tournamentSeries.setTimezone(LOCAL_TIME_ZONE);
        TournamentSeriesList tournamentSeriesList = new TournamentSeriesList();
        tournamentSeriesList.setData(Arrays.asList(tournamentSeries));
        when(mockMfoClient.getTournamentSeriesList()).thenReturn(tournamentSeriesList);

        TournamentList tournamentListResponse = new TournamentList();
        tournamentListResponse.setData(tournamentList);
        when(mockMfoClient.getTournamentList(1)).thenReturn(tournamentListResponse);

        tournament1 = createTournament(123, "Daily Qualifier 1", "2020-01-11T00:01:00+00:00",
                "2020-01-11T00:03:00+00:00");
        tournament1.setCurrentRound(2);

        tournament2 = createTournament(234,"Daily Qualifier 2", "2020-01-11T00:02:00+00:00",
                "2020-01-11T00:03:00+00:00");
        tournament2.setCurrentRound(1);
    }

    @Test
    public void testDescribeCurrentTournamentsWithEmptyTournamentList() {
        assertEquals("There are no active tournaments.", informer.describeCurrentTournaments());
    }

    @Test
    public void testDescribeCurrentTournamentsWithSingletonTournamentList() {
        tournamentList.add(tournament1);
        assertEquals("Daily Qualifier 1", informer.describeCurrentTournaments());
    }

    @Test
    public void testDescribeCurrentTournamentsWithMultipleActiveTournaments() {
        tournamentList.add(tournament1);
        tournamentList.add(tournament2);
        assertEquals("Daily Qualifier 1 and Daily Qualifier 2.", informer.describeCurrentTournaments());
    }

    @Test
    public void testDescribeCurrentTournamentsWithTournamentThatHasNotStartedIsEmptyList() {
        tournamentList.add(createTournament(123, "Daily Qualifier 1", "2020-01-11T00:04:00+00:00",
                "2020-01-11T00:03:00+00:00"));
        assertEquals("There are no active tournaments.", informer.describeCurrentTournaments());
    }

    @Test
    public void testDescribeCurrentTournamentsWithTournamentNotUpdatedInTheLastTwoHours() {
        tournamentList.add(createTournament(123, "Daily Qualifier 1", "2020-01-11T00:01:00+00:00",
                "2020-01-10T21:03:00+00:00"));
        assertEquals("There are no active tournaments.", informer.describeCurrentTournaments());
    }

    @Test
    public void testGetCurrentDecklistsWithEmptyTournamentList() {
        assertEquals("There are no active tournaments.", informer.getCurrentDecklists());
    }

    @Test
    public void testGetCurrentDecklistsWithSingletonTournamentList() {
        tournamentList.add(tournament1);
        assertEquals("https://my.cfbevents.com/deck/123", informer.getCurrentDecklists());
    }

    @Test
    public void testGetCurrentDecklistsWithMultipleActiveTournaments() {
        tournamentList.add(tournament1);
        tournamentList.add(tournament2);
        assertEquals("Daily Qualifier 1: https://my.cfbevents.com/deck/123 and "
                + "Daily Qualifier 2: https://my.cfbevents.com/deck/234", informer.getCurrentDecklists());
    }

    @Test
    public void testGetCurrentPairingsWithEmptyTournamentList() {
        assertEquals("There are no active tournaments.", informer.getCurrentPairings());
    }

    @Test
    public void testGetCurrentPairingsWithSingletonTournamentList() {
        tournamentList.add(tournament1);
        assertEquals("https://my.cfbevents.com/pairings/123", informer.getCurrentPairings());
    }

    @Test
    public void testGetCurrentPairingsWithMultipleActiveTournaments() {
        tournamentList.add(tournament1);
        tournamentList.add(tournament2);
        assertEquals("Daily Qualifier 1: https://my.cfbevents.com/pairings/123 and "
                        + "Daily Qualifier 2: https://my.cfbevents.com/pairings/234", informer.getCurrentPairings());
    }

    @Test
    public void testGetCurrentStandingsWithEmptyTournamentList() {
        assertEquals("There are no active tournaments.", informer.getCurrentStandings());
    }

    @Test
    public void testGetCurrentStandingsWithSingletonTournamentList() {
        tournamentList.add(tournament1);
        assertEquals("https://my.cfbevents.com/standings/123", informer.getCurrentStandings());
    }

    @Test
    public void testGetCurrentStandingsWithMultipleActiveTournaments() {
        tournamentList.add(tournament1);
        tournamentList.add(tournament2);
        assertEquals("Daily Qualifier 1: https://my.cfbevents.com/standings/123 and "
                        + "Daily Qualifier 2: https://my.cfbevents.com/standings/234", informer.getCurrentStandings());
    }

    @Test
    public void testGetCurrentRoundWithEmptyTournamentList() {
        assertEquals("There are no active tournaments.", informer.getCurrentRound());
    }

    @Test
    public void testGetCurrentRoundWithSingletonTournamentList() {
        tournamentList.add(tournament1);
        assertEquals("round 2", informer.getCurrentRound());
    }

    @Test
    public void testGetCurrentRoundWithMultipleActiveTournaments() {
        tournamentList.add(tournament1);
        tournamentList.add(tournament2);
        assertEquals("Daily Qualifier 1: round 2 and Daily Qualifier 2: round 1", informer.getCurrentRound());
    }


    private Tournament createTournament(final int id, final String name, final String startsAt, final String lastUpdatedAt) {
        Tournament tournament = new Tournament();
        tournament.setId(id);
        tournament.setName(name);
        tournament.setStartsAt(startsAt);
        tournament.setLastUpdated(convertToUtc(lastUpdatedAt, LOCAL_TIME_ZONE));
        return tournament;
    }

    private Instant convertToInstant(final String time, final String timeZone) {
        return LocalDateTime.parse(time, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                .atZone(ZoneId.of(timeZone)).toInstant();
    }

    private String convertToUtc(final String time, final String timeZone) {
        return convertToString(convertToInstant(time, timeZone));
    }

    private String convertToString(final Instant instant) {
        return DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(ZoneId.of("UTC")).format(instant);
    }
}
