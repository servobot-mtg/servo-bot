package com.ryan_mtg.servobot.model.schedule;

import lombok.Getter;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZonedDateTime;

@Getter
public class WeeklyTime {
    private DayOfWeek dayOfWeek;
    private LocalTime localTime;

    public WeeklyTime(final DayOfWeek dayOfWeek, final LocalTime localTime) {
        this.dayOfWeek = dayOfWeek;
        this.localTime = localTime;
    }

    public ZonedDateTime nextTime(final ZonedDateTime time) {
        ZonedDateTime nextTime = time.withHour(localTime.getHour()).withMinute(localTime.getMinute());
        while (nextTime.compareTo(time) < 0 || nextTime.getDayOfWeek() != dayOfWeek) {
            nextTime = nextTime.plusDays(1);
        }
        return nextTime;
    }
}
