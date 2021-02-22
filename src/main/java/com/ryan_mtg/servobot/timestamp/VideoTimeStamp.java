package com.ryan_mtg.servobot.timestamp;

import lombok.Data;

import java.time.Instant;

@Data
public class VideoTimeStamp {
    private static final int UNREGISTERED_ID = 0;

    private int id;
    private Instant time;
    private String channel;
    private String user;
    private String link;
    private String note;
    private String offset;
}