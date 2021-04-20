package com.ryan_mtg.servobot.model.schedule;

import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZonedDateTime;

@Getter @Setter
public class WeeklyStream {
    public static final int UNREGISTERED_ID = 0;

    private int id;
    private String name;
    private String announcement;
    private final DayOfWeek day;
    private final LocalTime time;

    public WeeklyStream(final int id, final String name, final String announcement, final DayOfWeek day,
            final LocalTime time) {
        this.id = id;
        this.name = name;
        this.announcement = announcement;
        this.day = day;
        this.time = time;
    }

    public ZonedDateTime nextTime(final ZonedDateTime now) {
        ZonedDateTime nextTime = now.withHour(time.getHour()).withMinute(time.getMinute());
        while (nextTime.compareTo(now) < 0 || nextTime.getDayOfWeek() != day) {
            nextTime = nextTime.plusDays(1);
        }
        return nextTime;
    }
}