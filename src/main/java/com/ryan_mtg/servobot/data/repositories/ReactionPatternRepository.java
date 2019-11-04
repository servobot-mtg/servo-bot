package com.ryan_mtg.servobot.data.repositories;

import com.ryan_mtg.servobot.data.models.ReactionPatternRow;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReactionPatternRepository extends CrudRepository<ReactionPatternRow, Integer> {
    Iterable<ReactionPatternRow> findAllByReactionId(final int commandId);
}
