package com.ryan_mtg.servobot.data.repositories;

import com.ryan_mtg.servobot.data.models.LoggedMessageRow;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoggedMessageRepository extends CrudRepository<LoggedMessageRow, Integer> {
}
