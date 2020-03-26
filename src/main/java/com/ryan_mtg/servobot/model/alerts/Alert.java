package com.ryan_mtg.servobot.model.alerts;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Duration;

@AllArgsConstructor
public class Alert {
    @Getter
    private Duration delay;

    @Getter
    private String token;
}
