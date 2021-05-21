package com.ryan_mtg.servobot.model.schedule;

import lombok.Getter;
import lombok.Setter;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class Schedule {
    private static final int UNREGISTERED_ID = 0;

    @Getter @Setter
    private int id;

    @Getter
    private String defaultAnnouncement;

    @Getter
    private List<WeeklyStream> weeklyStreams;

    private String timeZone;

    public Schedule(final String timeZone) {
        this(UNREGISTERED_ID, timeZone, null, new ArrayList<>());
    }

    public Schedule(final int id, final String timeZone, final String defaultAnnouncement,
            final List<WeeklyStream> weeklyStreams) {
        this.id = id;
        this.timeZone = timeZone;
        this.defaultAnnouncement = defaultAnnouncement;
        this.weeklyStreams = weeklyStreams;
    }

    public ZonedDateTime getNextStream() {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of(timeZone));
        return getWeeklyStreams().stream().filter(weeklyStream -> weeklyStream.isEnabled())
                .map(weeklyStream -> weeklyStream.nextTime(now)).min(ZonedDateTime::compareTo).get();
    }

    public void setTimeZone(final String timeZone) {
        this.timeZone = timeZone;
    }

    public ScheduleEdit addWeeklyStream(final WeeklyStream weeklyStream) {
        ScheduleEdit scheduleEdit = new ScheduleEdit();
        weeklyStreams.add(weeklyStream);
        scheduleEdit.saveWeeklyStream(id, weeklyStream);
        return scheduleEdit;
    }

    public ScheduleEdit deleteWeeklyStream(final int weeklyStreamId) {
        WeeklyStream weeklyStream = getWeeklyStream(weeklyStreamId);
        ScheduleEdit scheduleEdit = new ScheduleEdit();
        scheduleEdit.deleteWeeklyStream(weeklyStream);
        weeklyStreams.remove(weeklyStream);
        return scheduleEdit;
    }

    public WeeklyStream getWeeklyStream(final int weeklyStreamId) {
        return weeklyStreams.stream().filter(stream -> stream.getId() == weeklyStreamId).findFirst().get();
    }
}