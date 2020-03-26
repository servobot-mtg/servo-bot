package com.ryan_mtg.servobot.data.models;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "service_home")
@Getter @Setter
public class ServiceHomeRow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private int id;

    @Column(name = "bot_home_id")
    private int botHomeId;

    @Column(name = "service_type")
    private int serviceType;

    @Column(name = "long_value")
    private long longValue;
}
