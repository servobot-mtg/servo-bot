package com.ryan_mtg.servobot.tournament.mtgmelee;

import com.ryan_mtg.servobot.tournament.Tournament;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.time.Clock;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MtgMeleeInformerTest {
    private MtgMeleeWebParser mockWebParser;
    private MtgMeleeClient mockClient;
    private Clock mockClock;

    private MtgMeleeInformer informer;

    private MtgMeleeTournament tournament1 = new MtgMeleeTournament();

    @Before
    public void setUp() {
        mockWebParser = mock(MtgMeleeWebParser.class);
        mockClient = mock(MtgMeleeClient.class);
        mockClock = mock(Clock.class);
        informer = new MtgMeleeInformer(mockWebParser, mockClient, mockClock);

        tournament1.setId(2166);
        tournament1.setName("Tournament 1");
        tournament1.setPairingsIdMap(Collections.singletonMap(2, 7676));
    }

    @Test
    public void testDescribeCurrentTournamentsWithSingletonTournamentList() {
        when(mockWebParser.parse(tournament1.getId())).thenReturn(tournament1);
        assertEquals("Tournament 1.", informer.describeCurrentTournaments());
    }

    @Test
    public void testGetCurrentDecklistsWithSingletonTournamentList() {
        when(mockWebParser.parse(tournament1.getId())).thenReturn(tournament1);
        assertEquals("Tournament 1: https://mtgmelee.com/Tournament/View/2166", informer.getCurrentDecklists());
    }

    @Test
    public void testGetCurrentPairingsWithSingletonTournamentList() {
        when(mockWebParser.parse(tournament1.getId())).thenReturn(tournament1);
        assertEquals(
                "Tournament 1: https://mtgmelee.com/Tournament/View/2166#pairings-round-selector-container",
                informer.getCurrentPairings());
    }

    @Test
    public void testGetCurrentStandingsWithSingletonTournamentList() {
        when(mockWebParser.parse(tournament1.getId())).thenReturn(tournament1);
        assertEquals(
                "Tournament 1: https://mtgmelee.com/Tournament/View/2166#standings-phase-selector-container",
                informer.getCurrentStandings());
    }

    @Test
    public void testGetCurrentRoundWithSingletonTournamentList() {
        when(mockWebParser.parse(tournament1.getId())).thenReturn(tournament1);
        assertEquals("Tournament 1: round 2", informer.getCurrentRound());
    }

    @Test @Ignore
    public void spike() {
        informer = new MtgMeleeInformer();
        System.out.println(informer.getCurrentRecords());
        Tournament tournament = informer.getTournament(tournament1.getId());
        assertEquals(4, tournament.getStandings().getRound());
        assertNotNull(tournament.getMostRecentPairings());
    }
}
