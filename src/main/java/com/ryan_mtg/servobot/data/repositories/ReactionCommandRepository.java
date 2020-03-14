package com.ryan_mtg.servobot.data.repositories;

import com.ryan_mtg.servobot.data.models.ReactionCommandRow;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReactionCommandRepository extends CrudRepository<ReactionCommandRow, Integer> {
    Iterable<ReactionCommandRow> findAllByReactionId(int commandId);

    Iterable<ReactionCommandRow> findAllByReactionIdIn(List<Integer> reactionIds);
}
