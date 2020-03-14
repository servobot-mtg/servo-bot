package com.ryan_mtg.servobot.data.repositories;

import com.ryan_mtg.servobot.data.models.ReactionCommandRow;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReactionCommandRepository extends CrudRepository<ReactionCommandRow, Integer> {
    Iterable<ReactionCommandRow> findAllByReactionIdIn(Iterable<Integer> reactionIds);
}
