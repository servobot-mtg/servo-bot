package com.ryan_mtg.servobot.data.repositories;

import com.ryan_mtg.servobot.data.models.ScheduleRow;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleRepository extends CrudRepository<ScheduleRow, Integer> {
    ScheduleRow findByBotHomeId(int botHomeId);
}