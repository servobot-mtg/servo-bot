package com.ryan_mtg.servobot.data.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "game_queue_entry")
@IdClass(GameQueueEntryRow.GameQueueEntryRowId.class)
public class GameQueueEntryRow {
    @Id
    @Column(name = "game_queue_id")
    private int gameQueueId;

    @Id
    private int spot;

    @Column(name = "user_id")
    private int userId;

    public int getGameQueueId() {
        return gameQueueId;
    }

    public void setGameQueueId(final int gameQueueId) {
        this.gameQueueId = gameQueueId;
    }

    public int getSpot() {
        return spot;
    }

    public void setSpot(final int spot) {
        this.spot = spot;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(final int userId) {
        this.userId = userId;
    }

    public static class GameQueueEntryRowId implements Serializable {
        private int gameQueueId;
        private int spot;

        public GameQueueEntryRowId() {}

        public GameQueueEntryRowId(final int gameQueueId, final int spot) {
            this.gameQueueId = gameQueueId;
            this.spot = spot;
        }

        public int getGameQueueId() {
            return gameQueueId;
        }

        public int getSpot() {
            return spot;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            GameQueueEntryRowId that = (GameQueueEntryRowId) o;
            return gameQueueId == that.gameQueueId &&
                    spot == that.spot;
        }

        @Override
        public int hashCode() {
            return Objects.hash(gameQueueId, spot);
        }
    }
}
