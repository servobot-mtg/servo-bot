package com.ryan_mtg.servobot.data.models;

import com.ryan_mtg.servobot.utility.Validation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

@Entity
@Table(name = "suggestion")
@Getter @Setter
@NoArgsConstructor
public class SuggestionRow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Size(max = Validation.MAX_TRIGGER_LENGTH)
    private String alias;

    private int count;

    public SuggestionRow(final String alias, final int count) {
        this.alias = alias;
        this.count = count;
    }
}
