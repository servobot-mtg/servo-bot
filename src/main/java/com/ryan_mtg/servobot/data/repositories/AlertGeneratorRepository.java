package com.ryan_mtg.servobot.data.repositories;

import com.ryan_mtg.servobot.data.models.AlertGeneratorRow;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlertGeneratorRepository extends CrudRepository<AlertGeneratorRow, Integer> {
    Iterable<AlertGeneratorRow> findByBotHomeId(int botHomeId);
}
