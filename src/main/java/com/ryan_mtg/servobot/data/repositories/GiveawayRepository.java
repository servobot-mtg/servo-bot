package com.ryan_mtg.servobot.data.repositories;

import com.ryan_mtg.servobot.data.models.GiveawayRow;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GiveawayRepository extends CrudRepository<GiveawayRow, Integer> {
    GiveawayRow findById(int id);
    Iterable<GiveawayRow> findAllByIdIn(Iterable<Integer> ids);
    Iterable<GiveawayRow> findAllByBotHomeId(int botHomeId);

}
