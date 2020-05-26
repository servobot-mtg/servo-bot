package com.ryan_mtg.servobot.data.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "logged_message")
@Getter @Setter
public class LoggedMessageRow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "user_id")
    private int userId;

    private String message;

    @Column(name = "service_type")
    private int serviceType;

    private int direction;

    @Column(name = "sent_time")
    private long sentTime;
}
