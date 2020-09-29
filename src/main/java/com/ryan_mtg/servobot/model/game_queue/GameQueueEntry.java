package com.ryan_mtg.servobot.model.game_queue;

import com.ryan_mtg.servobot.user.HomedUser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter @Setter
@AllArgsConstructor
public class GameQueueEntry implements Comparable<GameQueueEntry> {
    private HomedUser user;
    private Instant enqueueTime;
    private PlayerState state;

    @Override
    public int compareTo(final GameQueueEntry o) {
        int result = enqueueTime.compareTo(o.enqueueTime);
        if (result != 0) {
            return  result;
        }
        return user.getId() - o.user.getId();
    }
}
