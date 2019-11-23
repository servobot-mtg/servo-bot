package com.ryan_mtg.servobot.data.repositories;

import com.ryan_mtg.servobot.data.models.UserRow;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<UserRow, Integer> {
    UserRow findByTwitchId(int twitchId);
    UserRow findById(int twitchId);
}
