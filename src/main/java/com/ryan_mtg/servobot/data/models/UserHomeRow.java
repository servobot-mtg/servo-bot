package com.ryan_mtg.servobot.data.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "user_home")
@IdClass(UserHomeRow.UserHomeId.class)
public class UserHomeRow {
    @Id
    @Column(name = "user_id")
    private int userId;

    @Id
    @Column(name = "bot_home_id")
    private int botHomeId;

    private int state;

    public int getUserId() {
        return userId;
    }

    public void setUserId(final int userId) {
        this.userId = userId;
    }

    public int getBotHomeId() {
        return botHomeId;
    }

    public void setBotHomeId(final int botHomeId) {
        this.botHomeId = botHomeId;
    }

    public int getState() {
        return state;
    }

    public void setState(final int state) {
        this.state = state;
    }

    public static class UserHomeId implements Serializable {
        private int userId;

        private int botHomeId;

        public UserHomeId() {}

        public int getUserId() {
            return userId;
        }

        public void setUserId(final int userId) {
            this.userId = userId;
        }

        public int getBotHomeId() {
            return botHomeId;
        }

        public void setBotHomeId(final int botHomeId) {
            this.botHomeId = botHomeId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o){
                return true;
            }
            if (o == null || getClass() != o.getClass()){
                return false;
            }

            UserHomeId that = (UserHomeId) o;
            return userId == that.userId && botHomeId == that.botHomeId;
        }

        @Override
        public int hashCode() {
            return Objects.hash(userId, botHomeId);
        }
    }
}
