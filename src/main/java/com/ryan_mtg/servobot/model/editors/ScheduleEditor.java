package com.ryan_mtg.servobot.model.editors;


import com.ryan_mtg.servobot.data.factories.ScheduleSerializer;
import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.model.schedule.Schedule;
import com.ryan_mtg.servobot.model.schedule.ScheduleEdit;
import com.ryan_mtg.servobot.model.schedule.WeeklyStream;
import com.ryan_mtg.servobot.utility.Strings;
import com.ryan_mtg.servobot.utility.Validation;
import lombok.RequiredArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;

@RequiredArgsConstructor
public class ScheduleEditor {
    private final int contextId;
    private final Schedule schedule;
    private final ScheduleSerializer scheduleSerializer;

    public WeeklyStream addWeeklyStream(final String name, final String announcement, final int day, final int time)
            throws UserError {
        Validation.validateRange(time, "Time", 1, 24 * 60 * 60);

        WeeklyStream weeklyStream = new WeeklyStream(WeeklyStream.UNREGISTERED_ID, name,
                Strings.isBlank(announcement) ? null : announcement, true, DayOfWeek.of(day),
                LocalTime.ofSecondOfDay(time));

        ScheduleEdit scheduleEdit = schedule.addWeeklyStream(weeklyStream);
        scheduleSerializer.commit(scheduleEdit);
        return weeklyStream;
    }

    public void deleteWeeklyStream(final int weeklyScheduleId) {
        ScheduleEdit scheduleEdit = schedule.deleteWeeklyStream(weeklyScheduleId);
        scheduleSerializer.commit(scheduleEdit);
    }

    public void updateWeeklyStreamEnabled(final int weeklyStreamId, final boolean enabled) {
        ScheduleEdit scheduleEdit = new ScheduleEdit();
        WeeklyStream weeklyStream = schedule.getWeeklyStream(weeklyStreamId);
        weeklyStream.setEnabled(enabled);
        scheduleEdit.saveWeeklyStream(schedule.getId(), weeklyStream);
        scheduleSerializer.commit(scheduleEdit);
    }
}
