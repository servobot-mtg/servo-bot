package com.ryan_mtg.servobot.data.repositories;

import com.ryan_mtg.servobot.data.models.StatementRow;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatementRepository extends CrudRepository<StatementRow, Integer> {
    Iterable<StatementRow> findAllByBookIdIn(Iterable<Integer> bookIds);
}
