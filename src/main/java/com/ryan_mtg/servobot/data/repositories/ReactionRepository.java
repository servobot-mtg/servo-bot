package com.ryan_mtg.servobot.data.repositories;

import com.ryan_mtg.servobot.data.models.ReactionRow;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReactionRepository extends CrudRepository<ReactionRow, Integer> {
    Iterable<ReactionRow> findAllByBotHomeId(int botHomeId);
}
