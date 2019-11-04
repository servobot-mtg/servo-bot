package com.ryan_mtg.servobot.data.models;

import com.ryan_mtg.servobot.discord.commands.CommandEvent;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "command_event")
public class CommandEventRow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "command_id")
    private int commandId;

    @Column(name = "event_type")
    private CommandEvent.Type eventType;

    public CommandEventRow() {
    }

    public CommandEventRow(final int id, final int commandId, final CommandEvent.Type eventType) {
        this.id = id;
        this.commandId = commandId;
        this.eventType = eventType;
    }

    public int getId() {
        return id;
    }

    public int getCommandId() {
        return commandId;
    }

    public void setCommandId(final int commandId) {
        this.commandId = commandId;
    }

    public CommandEvent.Type getEventType() {
        return eventType;
    }

    public void setEventType(final CommandEvent.Type eventType) {
        this.eventType = eventType;
    }
}
