package com.ryan_mtg.servobot.mpl;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Schedule {
    private Player player;
    private List<Match> matches;

    public Schedule(final Player player) {
        this.player = player;
        this.matches = new ArrayList<>();
    }
}
