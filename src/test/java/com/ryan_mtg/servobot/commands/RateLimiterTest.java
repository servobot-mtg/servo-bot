package com.ryan_mtg.servobot.commands;

import com.ryan_mtg.servobot.commands.hierarchy.RateLimit;
import org.junit.Before;
import org.junit.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RateLimiterTest {
    private static final int USER_ID = 123;
    private static final int OTHER_USER_ID = 234;
    private static final int COMMAND_ID = 456;
    private static Instant firstAccessTime = Instant.ofEpochMilli(1000000000);
    private static Instant secondAccessTime = firstAccessTime.plus(60, SECONDS);

    private RateLimiter rateLimiter;
    private Clock mockClock;

    @Before
    public void setUp() {
        mockClock = mock(Clock.class);
        when(mockClock.instant()).thenReturn(firstAccessTime, secondAccessTime);
        rateLimiter = new RateLimiter(mockClock);
    }

    @Test
    public void testAllowsFirstAccess() {
        assertTrue(rateLimiter.allow(USER_ID, COMMAND_ID, new RateLimit()));
    }

    @Test
    public void testAllowsSecondAccess() {
        RateLimit rateLimit = mock(RateLimit.class);
        when(rateLimit.allowAll()).thenReturn(true);
        assertTrue(rateLimiter.allow(USER_ID, COMMAND_ID, rateLimit));
        assertTrue(rateLimiter.allow(USER_ID, COMMAND_ID, rateLimit));
    }

    @Test
    public void testDoesNotAllowSecondAccessWhenWithinGlobalDuration() {
        RateLimit rateLimit = new RateLimit();
        rateLimit.setGlobalDuration(Duration.ofSeconds(120));
        assertTrue(rateLimiter.allow(USER_ID, COMMAND_ID, rateLimit));
        assertFalse(rateLimiter.allow(OTHER_USER_ID, COMMAND_ID, rateLimit));
    }
}