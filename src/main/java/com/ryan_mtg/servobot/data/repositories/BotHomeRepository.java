package com.ryan_mtg.servobot.data.repositories;

import com.ryan_mtg.servobot.data.models.BotHomeRow;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BotHomeRepository extends CrudRepository<BotHomeRow, Integer> {
    @Override
    Iterable<BotHomeRow> findAll();

    BotHomeRow findById(int id);
}
