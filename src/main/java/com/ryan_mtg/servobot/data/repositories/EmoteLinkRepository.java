package com.ryan_mtg.servobot.data.repositories;

import com.ryan_mtg.servobot.data.models.EmoteLinkRow;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmoteLinkRepository extends CrudRepository<EmoteLinkRow, Integer> {
    Iterable<EmoteLinkRow> findAllByBotHomeId(int botHomeId);
}
