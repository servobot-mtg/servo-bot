package com.ryan_mtg.servobot.data.repositories;

import com.ryan_mtg.servobot.data.models.StatementRow;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StatementRepository extends CrudRepository<StatementRow, Integer> {
    List<StatementRow> findAllByBookId(int id);
}
