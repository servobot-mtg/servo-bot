package com.ryan_mtg.servobot.data.repositories;

import com.ryan_mtg.servobot.data.models.ChatDraftPickRow;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatDraftPickRepository extends CrudRepository<ChatDraftPickRow, Integer> {
    Iterable<ChatDraftPickRow> findAllByChatDraftIdIn(Iterable<Integer> chatDraftIds);
    void deleteByIdIn(Iterable<Integer> ids);
}
