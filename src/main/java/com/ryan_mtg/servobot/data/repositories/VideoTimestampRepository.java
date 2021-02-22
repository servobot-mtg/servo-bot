package com.ryan_mtg.servobot.data.repositories;

import com.ryan_mtg.servobot.data.models.VideoTimestampRow;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoTimestampRepository extends CrudRepository<VideoTimestampRow, Integer> {
}
