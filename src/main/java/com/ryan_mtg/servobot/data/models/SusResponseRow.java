package com.ryan_mtg.servobot.data.models;

import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "sus_response")
@Getter
public class SusResponseRow {
    @Id
    private int id;

    private int weight;

    private String response;
}
