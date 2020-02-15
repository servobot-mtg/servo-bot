package com.ryan_mtg.servobot.data.models;

import com.ryan_mtg.servobot.model.GameQueue;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

@Entity
@Table(name = "game_queue")
public class GameQueueRow {
    public static final int MAX_NAME_SIZE = 30;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "bot_home_id")
    private int botHomeId;

    @Size(max = MAX_NAME_SIZE)
    private String name;

    private GameQueue.State state;
    private int next;

    @Column(name = "current_player_id")
    private int currentPlayerId;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public GameQueue.State getState() {
        return state;
    }

    public void setState(final GameQueue.State state) {
        this.state = state;
    }

    public int getNext() {
        return next;
    }

    public void setNext(final int next) {
        this.next = next;
    }

    public int getCurrentPlayerId() {
        return currentPlayerId;
    }

    public void setCurrentPlayerId(final int currentPlayerId) {
        this.currentPlayerId = currentPlayerId;
    }
}
