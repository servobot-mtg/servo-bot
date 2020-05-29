package com.ryan_mtg.servobot.data.repositories;

import com.ryan_mtg.servobot.data.models.GameQueueRow;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameQueueRepository extends CrudRepository<GameQueueRow, Integer> {
    GameQueueRow findById(int id);
    Iterable<GameQueueRow> findAllByBotHomeId(int botHomeId);
    Iterable<GameQueueRow> findAllByIdInOrCurrentPlayerIdIn(List<Integer> gameQueueIds, List<Integer> currentPlayerIds);
}
