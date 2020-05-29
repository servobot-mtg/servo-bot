package com.ryan_mtg.servobot.data.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "game_queue_entry")
@IdClass(GameQueueEntryRow.GameQueueEntryRowId.class)
@Getter
public class GameQueueEntryRow {
    @Id
    @Column(name = "game_queue_id")
    private int gameQueueId;

    @Id
    private int spot;

    @Column(name = "user_id")
    private int userId;

    public void setGameQueueId(final int gameQueueId) {
        this.gameQueueId = gameQueueId;
    }

    public void setSpot(final int spot) {
        this.spot = spot;
    }

    public void setUserId(final int userId) {
        this.userId = userId;
    }

    @Getter @EqualsAndHashCode @NoArgsConstructor
    public static class GameQueueEntryRowId implements Serializable {
        private int gameQueueId;
        private int spot;

        public GameQueueEntryRowId(final int gameQueueId, final int spot) {
            this.gameQueueId = gameQueueId;
            this.spot = spot;
        }
    }
}
