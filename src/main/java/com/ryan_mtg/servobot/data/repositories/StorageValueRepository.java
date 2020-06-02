package com.ryan_mtg.servobot.data.repositories;

import com.ryan_mtg.servobot.data.models.StorageValueRow;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StorageValueRepository extends CrudRepository<StorageValueRow, Integer> {
    Iterable<StorageValueRow> findByBotHomeId(int botHomeId);
    Iterable<StorageValueRow> findAllByUserId(Iterable<Integer> userIds);
    void deleteAllByBotHomeIdAndName(int botHomeId, String name);
}
