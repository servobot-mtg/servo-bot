package com.ryan_mtg.servobot.data.models;

import com.ryan_mtg.servobot.model.game_queue.Game;
import com.ryan_mtg.servobot.model.game_queue.GameQueue;
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
    private int id;

    @Column(name = "bot_home_id")
    private int botHomeId;

    private Game game;

    private int flags;

    @Column(name = "start_time")
    private Long startTime;

    @Size(max = Validation.MAX_NAME_LENGTH)
    private String code;

    @Size(max = Validation.MAX_NAME_LENGTH)
    private String server;

    @Size(max = Validation.MAX_CHANNEL_NAME_LENGTH)
    @Column(name = "proximity_server")
    private String proximityServer;

    @Column(name = "message_id")
    private long messageId;

    @Column(name = "channel_id")
    private long channelId;

    private GameQueue.State state;
}
