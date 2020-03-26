package com.ryan_mtg.servobot.data.repositories;

import com.ryan_mtg.servobot.data.models.GiveawayRow;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GiveawayRepository extends CrudRepository<GiveawayRow, Integer> {
    Iterable<GiveawayRow> findAllByBotHomeId(int botHomeId);
    GiveawayRow findById(int id);
}
