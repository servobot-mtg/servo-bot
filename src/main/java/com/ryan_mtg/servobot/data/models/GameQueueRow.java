package com.ryan_mtg.servobot.data.models;

import com.ryan_mtg.servobot.model.GameQueue;
import com.ryan_mtg.servobot.utility.Validation;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

@Entity
@Table(name = "game_queue")
@Getter @Setter
public class GameQueueRow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private int id;

    @Column(name = "bot_home_id")
    private int botHomeId;

    @Size(max = Validation.MAX_NAME_LENGTH)
    private String name;

    private GameQueue.State state;
    private int next;

    @Column(name = "current_player_id")
    private int currentPlayerId;
}
