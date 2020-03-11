package com.ryan_mtg.servobot.utility;

import java.time.Duration;

public class TimeTest {
    public void testSeconds() {
        //TODO: come up with some test cases
        for(int i = 0; i < 3*24; i++) {
            System.out.println(String.format("%d: %s", i, Time.toReadableString(Duration.ofMinutes(i))));
        }
    }
}