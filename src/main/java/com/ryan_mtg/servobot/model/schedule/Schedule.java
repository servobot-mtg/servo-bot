package com.ryan_mtg.servobot.model.schedule;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

public class Schedule {
    private String timeZone;

    public Schedule(final String timeZone) {
        this.timeZone = timeZone;
    }

    public ZonedDateTime getNextStream() {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of(timeZone));
        return getDefaultWeeklyStreamTimes().stream().map(weeklyTime -> weeklyTime.nextTime(now))
                .min(ZonedDateTime::compareTo).get();
    }

    public void setTimeZone(final String timeZone) {
        this.timeZone = timeZone;
    }

    private List<WeeklyTime> getDefaultWeeklyStreamTimes() {
        return Arrays.asList(new WeeklyTime(DayOfWeek.WEDNESDAY, LocalTime.of(18, 00)),
                new WeeklyTime(DayOfWeek.SATURDAY, LocalTime.of(19, 00)));
    }
}