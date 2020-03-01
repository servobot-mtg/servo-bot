package com.ryan_mtg.servobot.data.models;

import com.ryan_mtg.servobot.utility.Validation;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

@Entity
@Table(name = "command_trigger")
public class TriggerRow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int type;

    @Column(name = "command_id")
    private int commandId;

    @Size(max = Validation.MAX_TRIGGER_LENGTH)
    private String text;

    public TriggerRow() {}

    public TriggerRow(final int id, final int type, final int commandId, final String text) {
        this.id = id;
        this.type = type;
        this.commandId = commandId;
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public int getType() {
        return type;
    }

    public int getCommandId() {
        return commandId;
    }

    public String getText() {
        return text;
    }
}
