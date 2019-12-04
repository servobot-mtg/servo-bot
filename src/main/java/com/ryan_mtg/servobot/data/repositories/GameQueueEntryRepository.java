package com.ryan_mtg.servobot.data.repositories;

import com.ryan_mtg.servobot.data.models.GameQueueEntryRow;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameQueueEntryRepository extends CrudRepository<GameQueueEntryRow, Integer> {
    Iterable<GameQueueEntryRow> findByGameQueueIdOrderBySpotAsc(int gameQueueId);
    void deleteAllByGameQueueId(int gameQueueId);
    void deleteAllByGameQueueIdAndUserId(int id, int userId);
}

