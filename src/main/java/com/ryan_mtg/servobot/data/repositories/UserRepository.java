package com.ryan_mtg.servobot.data.repositories;

import com.ryan_mtg.servobot.data.models.UserRow;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<UserRow, Integer> {
    UserRow findById(int id);
    UserRow findByTwitchId(int twitchId);
    UserRow findByDiscordId(long discordId);

    List<UserRow> findByArenaUsernameIsNotNull();

    @Query(value = "SELECT id FROM UserRow")
    Iterable<Integer> getAllIds();
}
