package com.ryan_mtg.servobot.data.models;

import com.ryan_mtg.servobot.commands.Permission;
import com.ryan_mtg.servobot.utility.Validation;

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
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int flags;

    private Permission permission;

    @Column(name = "rate_limit")
    //In seconds
    private int rateLimitDuration;

    private int type;

    @Column(name = "bot_home_id")
    private int botHomeId;

    @Size(max = Validation.MAX_TEXT_LENGTH)
    private String stringParameter;

    @Size(max = Validation.MAX_TEXT_LENGTH)
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

    public int getRateLimitDuration() {
        return rateLimitDuration;
    }

    public void setRateLimitDuration(int rateLimitDuration) {
        this.rateLimitDuration = rateLimitDuration;
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
