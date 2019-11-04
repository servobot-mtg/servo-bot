package com.ryan_mtg.servobot.data.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "home")
public class BotHomeRow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    private String homeName;

    @Column(name = "streamer_id")
    private long streamerId;

    public int getId() {
        return id;
    }

    public String getHomeName() {
        return homeName;
    }

    public void setHomeName(final String homeName) {
        this.homeName = homeName;
    }

    public long getStreamerId() {
        return streamerId;
    }

    public void setStreamerId(final long streamerId) {
        this.streamerId = streamerId;
    }
}
