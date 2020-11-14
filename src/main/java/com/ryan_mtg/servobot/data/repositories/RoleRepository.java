package com.ryan_mtg.servobot.data.repositories;

import com.ryan_mtg.servobot.data.models.RoleRow;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends CrudRepository<RoleRow, Integer> {
    Iterable<RoleRow> findAllByBotHomeId(int botHomeId);
}