package com.ryan_mtg.servobot.data.repositories;

import com.ryan_mtg.servobot.data.models.BookRow;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends CrudRepository<BookRow, Integer> {
    Iterable<BookRow> findAllByBotHomeId(int botHomeId);
}
