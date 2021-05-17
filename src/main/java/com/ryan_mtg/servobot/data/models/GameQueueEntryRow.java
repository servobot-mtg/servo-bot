package com.ryan_mtg.servobot.data.models;

import com.ryan_mtg.servobot.model.game_queue.PlayerState;
import com.ryan_mtg.servobot.utility.Validation;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Entity
@Table(name = "game_queue_entry")
@IdClass(GameQueueEntryRow.GameQueueEntryRowId.class)
@Getter @Setter
public class GameQueueEntryRow {
    @Id
    @Column(name = "game_queue_id")
    private int gameQueueId;

    @Id
    @Column(name = "user_id")
    private int userId;

    @Column(name = "enqueue_time")
    private long enqueueTime;

    @Size(max = Validation.MAX_TEXT_LENGTH)
    private String note;

    private PlayerState state;

    @Getter @EqualsAndHashCode @NoArgsConstructor
    public static class GameQueueEntryRowId implements Serializable {
        private int gameQueueId;
        private int userId;

        public GameQueueEntryRowId(final int gameQueueId, final int userId) {
            this.gameQueueId = gameQueueId;
            this.userId = userId;
        }
    }
}
