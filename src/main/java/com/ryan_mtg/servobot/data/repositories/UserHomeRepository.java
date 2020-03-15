package com.ryan_mtg.servobot.data.repositories;

import com.ryan_mtg.servobot.data.models.UserHomeRow;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserHomeRepository extends CrudRepository<UserHomeRow, UserHomeRow.UserHomeId> {
    UserHomeRow findByUserIdAndBotHomeId(int userId, int botHomeId);
    void deleteByUserIdAndBotHomeId(int userId, int botHomeId);

    List<UserHomeRow> findByUserId(int userId);
    Iterable<UserHomeRow> findByUserIdIn(Iterable<Integer> userIds);
    List<UserHomeRow> findByBotHomeId(int botHomeId);

}
