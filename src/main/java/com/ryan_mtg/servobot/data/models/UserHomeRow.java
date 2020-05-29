package com.ryan_mtg.servobot.data.models;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "user_home")
@IdClass(UserHomeRow.UserHomeId.class)
@Getter @Setter
public class UserHomeRow {
    @Id
    @Column(name = "user_id")
    private int userId;

    @Id
    @Column(name = "bot_home_id")
    private int botHomeId;

    private int state;

    @Data @NoArgsConstructor
    public static class UserHomeId implements Serializable {
        private int userId;
        private int botHomeId;
    }
}
