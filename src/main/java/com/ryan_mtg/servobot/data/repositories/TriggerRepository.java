package com.ryan_mtg.servobot.data.repositories;

import com.ryan_mtg.servobot.data.models.TriggerRow;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TriggerRepository extends CrudRepository<TriggerRow, Integer> {
    Iterable<TriggerRow> findAllByCommandId(int commandId);
}
