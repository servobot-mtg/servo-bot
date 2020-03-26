package com.ryan_mtg.servobot.data.models;

import com.ryan_mtg.servobot.utility.Validation;
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
@Table(name = "reaction_pattern")
@Getter @Setter
public class ReactionPatternRow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Size(max = Validation.MAX_PATTERN_LENGTH)
    private String pattern;

    @Column(name = "reaction_id")
    private int reactionId;
}
