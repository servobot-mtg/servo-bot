package com.ryan_mtg.servobot.data.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "service_home")
public class ServiceHomeRow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "bot_home_id")
    private int botHomeId;

    @Column(name = "service_type")
    private int serviceType;

    @Column(name = "long_value")
    private long longValue;

    public int getId() {
        return id;
    }

    public int getBotHomeId() {
        return botHomeId;
    }

    public int getServiceType() {
        return serviceType;
    }

    public long getLong() {
        return longValue;
    }
}
