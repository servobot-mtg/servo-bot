package com.ryan_mtg.servobot.data.repositories;

import com.ryan_mtg.servobot.data.models.CommandEventRow;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommandEventRepository extends CrudRepository<CommandEventRow, Integer> {
    Iterable<CommandEventRow> findAllByCommandId(final int commandId);
}
