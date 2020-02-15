package com.ryan_mtg.servobot.data.models;

import com.ryan_mtg.servobot.commands.Permission;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

@Entity
@Table(name = "command")
public class CommandRow {
    public static final int MAX_STRING_SIZE = 200;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int flags;

    private Permission permission;

    private int type;

    @Column(name = "bot_home_id")
    private int botHomeId;

    @Size(max = MAX_STRING_SIZE)
    private String stringParameter;

    @Size(max = MAX_STRING_SIZE)
    private String stringParameter2;

    private Long longParameter;

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public int getFlags() {
        return flags;
    }

    public void setFlags(final int flags) {
        this.flags = flags;
    }

    public Permission getPermission() {
        return permission;
    }

    public void setPermission(final Permission permission) {
        this.permission = permission;
    }

    public int getType() {
        return type;
    }

    public int getBotHomeId() {
        return botHomeId;
    }

    public void setBotHomeId(final int botHomeId) {
        this.botHomeId = botHomeId;
    }

    public void setType(final int type) {
        this.type = type;
    }

    public String getStringParameter() {
        return stringParameter;
    }

    public void setStringParameter(final String stringParameter) {
        this.stringParameter = stringParameter;
    }

    public String getStringParameter2() {
        return stringParameter2;
    }

    public void setStringParameter2(final String stringParameter2) {
        this.stringParameter2 = stringParameter2;
    }

    public Long getLongParameter() {
        return this.longParameter;
    }

    public void setLongParameter(final long longParameter) {
        this.longParameter = longParameter;
    }
}
