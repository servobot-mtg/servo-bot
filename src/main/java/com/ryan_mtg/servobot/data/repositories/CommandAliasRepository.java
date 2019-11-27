package com.ryan_mtg.servobot.data.repositories;

import com.ryan_mtg.servobot.data.models.CommandAliasRow;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommandAliasRepository extends CrudRepository<CommandAliasRow, Integer> {
    Iterable<CommandAliasRow> findAllByCommandId(final int commandId);

    void deleteCommandAliasById(int id);
}
