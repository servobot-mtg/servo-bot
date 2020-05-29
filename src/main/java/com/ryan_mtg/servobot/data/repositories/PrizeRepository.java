package com.ryan_mtg.servobot.data.repositories;

import com.ryan_mtg.servobot.data.models.PrizeRow;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrizeRepository extends CrudRepository<PrizeRow, Integer> {
    Iterable<PrizeRow> findAllByGiveawayIdIn(Iterable<Integer> giveawayIds);
    Iterable<PrizeRow> findAllByWinnerId(List<Integer> usersToDelete);
}
