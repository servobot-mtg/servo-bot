package com.ryan_mtg.servobot.model.schedule;

import lombok.Getter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Getter
public class ScheduleEdit {
    private final Map<Schedule, Integer> savedSchedules = new HashMap<>();
    private final Map<WeeklyStream, Integer> savedWeeklyStreams = new HashMap<>();
    private final Set<WeeklyStream> deletedWeeklyStreams = new HashSet<>();

    public void saveSchedule(final int contextId, final Schedule schedule) {
        savedSchedules.put(schedule, contextId);
    }

    public void saveWeeklyStream(final int scheduleId, final WeeklyStream weeklyStream) {
        savedWeeklyStreams.put(weeklyStream, scheduleId);
    }

    public void deleteWeeklyStream(final WeeklyStream weeklyStream) {
        deletedWeeklyStreams.add(weeklyStream);
    }
}
