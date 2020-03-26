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
@Table(name = "storage_value")
@Getter @Setter
public class StorageValueRow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "bot_home_id")
    private int botHomeId;

    private int type;

    @Column(name = "user_id")
    private int userId;

    @Size(max = Validation.MAX_NAME_LENGTH)
    private String name;

    private int number;

    @Size(max = Validation.MAX_TEXT_LENGTH)
    private String string;
}
