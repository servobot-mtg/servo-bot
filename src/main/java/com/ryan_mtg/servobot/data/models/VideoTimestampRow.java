package com.ryan_mtg.servobot.data.models;

import com.ryan_mtg.servobot.utility.Validation;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

@Entity
@Table(name = "video_timestamp")
@Getter @Setter
public class VideoTimestampRow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private long time;

    @Size(max = Validation.MAX_USERNAME_LENGTH)
    private String channel;

    @Size(max = Validation.MAX_USERNAME_LENGTH)
    private String user;

    @Size(max = Validation.MAX_TEXT_LENGTH)
    private String link;

    @Size(max = Validation.MAX_TEXT_LENGTH)
    private String note;

    @Size(max = Validation.MAX_NAME_LENGTH)
    private String offset;
}