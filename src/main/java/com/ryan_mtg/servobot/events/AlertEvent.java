package com.ryan_mtg.servobot.events;

public interface AlertEvent extends HomeEvent {
    String getAlertToken();
}
