package com.ryan_mtg.servobot.data.models;

import com.ryan_mtg.servobot.utility.Validation;
import lombok.AccessLevel;
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
@Table(name = "home")
@Getter @Setter
public class BotHomeRow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private int id;

    @Column(name = "name")
    @Size(max = Validation.MAX_NAME_LENGTH)
    private String homeName;

    @Column(name = "bot_name")
    @Size(max = Validation.MAX_NAME_LENGTH)
    private String botName;

    @Column(name = "time_zone")
    @Size(max = Validation.MAX_TIME_ZONE_LENGTH)
    private String timeZone;
}
