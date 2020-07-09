package com.ryan_mtg.servobot.utility;

import org.junit.Test;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.Assert.assertEquals;

public class TimeTest {
    private static ZonedDateTime TEST_DATE = ZonedDateTime.of(1955, 11, 12, 18, 38, 0, 0, ZoneId.of("UTC"));

    public void testSeconds() {
        //TODO: come up with some test cases
        for(int i = 0; i < 3*24; i++) {
            System.out.println(String.format("%d: %s", i, Time.toReadableString(Duration.ofMinutes(i))));
        }
    }

    @Test
    public void testGetTimeStringOnSameDay() {
        ZonedDateTime sameDay = ZonedDateTime.of(TEST_DATE.getYear(), TEST_DATE.getMonthValue(),
                TEST_DATE.getDayOfMonth(), 0, 0, 0, 0, TEST_DATE.getZone());
        assertEquals("6:38 PM", Time.toReadableString(TEST_DATE, sameDay));
    }

    @Test
    public void testGetTimeStringInSameMonth() {
        ZonedDateTime sameMonth = ZonedDateTime.of(TEST_DATE.getYear(), TEST_DATE.getMonthValue(), 1, 0, 0, 0,
                0, TEST_DATE.getZone());
        assertEquals("6:38 PM on the 12th", Time.toReadableString(TEST_DATE, sameMonth));
    }

    @Test
    public void testGetTimeStringInSameYear() {
        ZonedDateTime sameYear = ZonedDateTime.of(TEST_DATE.getYear(), 1, 1, 0, 0, 0,
                0, TEST_DATE.getZone());
        assertEquals("6:38 PM on the 12th of November", Time.toReadableString(TEST_DATE, sameYear));
    }

    @Test
    public void testGetTimeStringInDifferentYears() {
        ZonedDateTime differentYear = ZonedDateTime.of(1954, 1, 1, 0, 0, 0,
                0, TEST_DATE.getZone());
        assertEquals("6:38 PM on the 12th of November, 1955", Time.toReadableString(TEST_DATE, differentYear));
    }
}