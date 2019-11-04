package com.ryan_mtg.servobot.data.repositories;

import com.ryan_mtg.servobot.data.models.CommandRow;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommandRepository extends CrudRepository<CommandRow, Integer> {
    Iterable<CommandRow> findAllByBotHomeId(int botHomeId);
}
