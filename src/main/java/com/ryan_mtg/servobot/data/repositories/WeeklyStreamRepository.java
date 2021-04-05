package com.ryan_mtg.servobot.data.repositories;

import com.ryan_mtg.servobot.data.models.WeeklyStreamRow;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WeeklyStreamRepository extends CrudRepository<WeeklyStreamRow, Integer> {
    Iterable<WeeklyStreamRow> findAllByScheduleId(int scheduleId);
}
