package com.ryan_mtg.servobot.data.repositories;

import com.ryan_mtg.servobot.data.models.ChatDraftRow;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatDraftRepository extends CrudRepository<ChatDraftRow, Integer> {
    Iterable<ChatDraftRow> findAllByBotHomeId(int botHomeId);
}
