package com.ryan_mtg.servobot.data.repositories;

import com.ryan_mtg.servobot.data.models.RoleTableRow;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleTableRepository extends CrudRepository<RoleTableRow, Integer>{
    Iterable<RoleTableRow> findAllByBotHomeId(int botHomeId);
}