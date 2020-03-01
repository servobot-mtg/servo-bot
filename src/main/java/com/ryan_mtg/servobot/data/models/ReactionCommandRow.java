package com.ryan_mtg.servobot.data.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "reaction_command")
public class ReactionCommandRow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "reaction_id")
    private int reactionId;

    @Column(name = "command_id")
    private int commandId;

    public int getId() {
        return id;
    }

    public int getReactionId() {
        return reactionId;
    }

    public int getCommandId() {
        return commandId;
    }
}
