package com.ryan_mtg.servobot.data.models;

import com.ryan_mtg.servobot.commands.Permission;
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
@Table(name = "command")
@Getter @Setter
public class CommandRow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int type;

    @Column(name = "bot_home_id")
    private int botHomeId;

    private int flags;

    private Permission permission;

    @Column(name = "rate_limit")
    //In seconds
    private Integer rateLimitDuration;

    @Size(max = Validation.MAX_TEXT_LENGTH)
    private String stringParameter;

    @Size(max = Validation.MAX_TEXT_LENGTH)
    private String stringParameter2;

    private Long longParameter;

    public void setLongParameter(final long longParameter) {
        this.longParameter = longParameter;
    }
}
