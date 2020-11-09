package com.ryan_mtg.servobot.mpl;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Round {
    private int number;
    private List<Match> matches;

    public Round(final int number) {
        this.number = number;
        this.matches = new ArrayList<>();
    }
}
