package com.ryan_mtg.servobot.data.repositories;

import com.ryan_mtg.servobot.data.models.DraftEntrantRow;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DraftEntrantRepository extends CrudRepository<DraftEntrantRow, Integer> {
    Iterable<DraftEntrantRow> findAllByChatDraftIdIn(Iterable<Integer> chatDraftIds);
    void deleteByIdIn(Iterable<Integer> ids);
}