package com.ryan_mtg.servobot.data.repositories;

import com.ryan_mtg.servobot.data.models.BotRow;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BotRepository extends CrudRepository<BotRow, Integer> {
    @Override
    List<BotRow> findAll();

    default Optional<BotRow> findFirst() {
        return findAll().stream().findFirst();
    }
}
