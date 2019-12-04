package com.ryan_mtg.servobot.data.repositories;

import com.ryan_mtg.servobot.data.models.GameQueueRow;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameQueueRepository extends CrudRepository<GameQueueRow, Integer> {
    Iterable<GameQueueRow> findAllByBotHomeId(int botHomeId);
    GameQueueRow findById(int id);
}
