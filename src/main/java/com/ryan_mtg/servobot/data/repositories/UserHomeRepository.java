package com.ryan_mtg.servobot.data.repositories;

import com.ryan_mtg.servobot.data.models.UserHomeRow;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserHomeRepository extends CrudRepository<UserHomeRow, UserHomeRow.UserHomeId> {
    UserHomeRow findByUserIdAndBotHomeId(int userId, int botHomeId);
    void deleteByBotHomeIdAndUserId(int botHomeId, int userId);

    List<UserHomeRow> findByUserId(int userId);
    Iterable<UserHomeRow> findByUserIdIn(Iterable<Integer> userIds);

    Iterable<UserHomeRow> findByBotHomeIdAndUserIdIn(int botHomeId, Iterable<Integer> userIds);
    Iterable<UserHomeRow> findByBotHomeId(int botHomeId);

    @Query(value = "SELECT userId FROM UserHomeRow WHERE bot_home_id = :bot_home_id")
    Iterable<Integer> getAllUserIds(@Param("bot_home_id") int botHomeId);

}
