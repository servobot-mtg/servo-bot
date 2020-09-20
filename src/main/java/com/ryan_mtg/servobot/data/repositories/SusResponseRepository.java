package com.ryan_mtg.servobot.data.repositories;

import com.ryan_mtg.servobot.data.models.SusResponseRow;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SusResponseRepository extends CrudRepository<SusResponseRow, Integer> {
}
