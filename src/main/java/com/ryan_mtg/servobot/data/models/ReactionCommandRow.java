package com.ryan_mtg.servobot.data.models;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "reaction_command")
@Getter
public class ReactionCommandRow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "reaction_id")
    private int reactionId;

    @Column(name = "command_id")
    private int commandId;
}
