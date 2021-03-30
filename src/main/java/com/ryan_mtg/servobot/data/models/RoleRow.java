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
@Table(name = "role")
@Getter @Setter
public class RoleRow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "bot_home_id")
    private int botHomeId;

    private int flags;

    @Size(max = Validation.MAX_ROLE_LENGTH)
    private String role;

    @Column(name = "role_id")
    private long roleId;

    @Size(max = Validation.MAX_EMOTE_LENGTH)
    private String emote;
}
