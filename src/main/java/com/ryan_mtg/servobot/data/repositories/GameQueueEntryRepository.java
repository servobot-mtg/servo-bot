package com.ryan_mtg.servobot.data.repositories;

import com.ryan_mtg.servobot.data.models.GameQueueEntryRow;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameQueueEntryRepository
        extends CrudRepository<GameQueueEntryRow, GameQueueEntryRow.GameQueueEntryRowId> {
    Iterable<GameQueueEntryRow> findByGameQueueIdOrderBySpotAsc(int gameQueueId);
    Iterable<GameQueueEntryRow> findAllByUserIdIn(Iterable<Integer> userIds);

    void deleteAllByGameQueueId(int gameQueueId);
    void deleteAllByGameQueueIdAndUserId(int gameQueueId, int userId);
    void deleteAllByGameQueueIdAndSpot(int gameQueueId, int spot);
}

