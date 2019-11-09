package com.ryan_mtg.servobot.data.repositories;

import com.ryan_mtg.servobot.data.models.ServiceHomeRow;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceHomeRepository extends CrudRepository<ServiceHomeRow, Integer> {
    Iterable<ServiceHomeRow> findAllByBotHomeId(int botHomeId);
}
