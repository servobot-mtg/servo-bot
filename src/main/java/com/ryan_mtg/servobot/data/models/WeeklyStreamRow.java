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
import java.time.DayOfWeek;

@Entity
@Table(name = "weekly_stream")
@Getter @Setter
public class WeeklyStreamRow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "schedule_id")
    private int scheduleId;

    @Size(max = Validation.MAX_NAME_LENGTH)
    private String name;

    @Size(max = Validation.MAX_TEXT_LENGTH)
    private String announcement;

    private DayOfWeek day;
    private int time;
}
