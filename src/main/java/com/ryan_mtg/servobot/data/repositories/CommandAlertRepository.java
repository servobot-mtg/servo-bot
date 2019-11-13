package com.ryan_mtg.servobot.data.repositories;

import com.ryan_mtg.servobot.data.models.CommandAlertRow;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommandAlertRepository extends CrudRepository<CommandAlertRow, Integer> {
    Iterable<CommandAlertRow> findAllByCommandId(final int commandId);
}
