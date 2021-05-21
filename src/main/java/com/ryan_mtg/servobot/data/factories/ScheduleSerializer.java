package com.ryan_mtg.servobot.data.factories;

import com.ryan_mtg.servobot.data.models.ScheduleRow;
import com.ryan_mtg.servobot.data.models.WeeklyStreamRow;
import com.ryan_mtg.servobot.data.repositories.ScheduleRepository;
import com.ryan_mtg.servobot.data.repositories.WeeklyStreamRepository;
import com.ryan_mtg.servobot.model.schedule.Schedule;
import com.ryan_mtg.servobot.model.schedule.ScheduleEdit;
import com.ryan_mtg.servobot.model.schedule.WeeklyStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Component @RequiredArgsConstructor
public class ScheduleSerializer {
    private final ScheduleRepository scheduleRepository;
    private final WeeklyStreamRepository weeklyStreamRepository;

    public Schedule createSchedule(final int botHomeId, final String timeZone) {
        ScheduleRow scheduleRow = scheduleRepository.findByBotHomeId(botHomeId);
        int scheduleId = scheduleRow.getId();
        List<WeeklyStream> weeklyStreams = new ArrayList<>();
        for (WeeklyStreamRow weeklyStreamRow : weeklyStreamRepository.findAllByScheduleId(scheduleId)) {
            weeklyStreams.add(createWeeklyStream(weeklyStreamRow));
        }
        return new Schedule(scheduleId, timeZone, scheduleRow.getDefaultAnnouncement(), weeklyStreams);
    }

    @Transactional(rollbackOn = Exception.class)
    public void commit(final ScheduleEdit scheduleEdit) {
        scheduleEdit.getDeletedWeeklyStreams()
                .forEach(weeklyStream -> weeklyStreamRepository.deleteById(weeklyStream.getId()));

        scheduleEdit.getSavedSchedules().forEach((schedule, botHomeId) -> saveSchedule(botHomeId, schedule));

        scheduleEdit.getSavedWeeklyStreams()
                .forEach((weeklyStream, scheduleId) -> saveWeeklyStream(scheduleId, weeklyStream));
    }

    private void saveSchedule(final int botHomeId, final Schedule schedule) {
        ScheduleRow scheduleRow = new ScheduleRow();

        scheduleRow.setId(schedule.getId());
        scheduleRow.setBotHomeId(botHomeId);
        scheduleRow.setDefaultAnnouncement(schedule.getDefaultAnnouncement());

        scheduleRepository.save(scheduleRow);

        schedule.setId(scheduleRow.getId());
    }

    private void saveWeeklyStream(final int scheduleId, final WeeklyStream weeklyStream) {
        WeeklyStreamRow weeklyStreamRow = new WeeklyStreamRow();
        weeklyStreamRow.setId(weeklyStream.getId());
        weeklyStreamRow.setScheduleId(scheduleId);
        weeklyStreamRow.setName(weeklyStream.getName());
        weeklyStreamRow.setAnnouncement(weeklyStream.getAnnouncement());
        weeklyStreamRow.setDay(weeklyStream.getDay());
        weeklyStreamRow.setTime(weeklyStream.getTime().toSecondOfDay());

        weeklyStreamRepository.save(weeklyStreamRow);

        weeklyStream.setId(weeklyStreamRow.getId());
    }

    private WeeklyStream createWeeklyStream(final WeeklyStreamRow weeklyStreamRow) {
        return new WeeklyStream(weeklyStreamRow.getId(), weeklyStreamRow.getName(), weeklyStreamRow.getAnnouncement(),
                weeklyStreamRow.isEnabled(), weeklyStreamRow.getDay(),
                LocalTime.ofSecondOfDay(weeklyStreamRow.getTime()));
    }
}