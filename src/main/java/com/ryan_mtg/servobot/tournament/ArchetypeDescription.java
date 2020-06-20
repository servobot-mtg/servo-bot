package com.ryan_mtg.servobot.tournament;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class ArchetypeDescription implements Comparable<ArchetypeDescription> {
    private String name;
    private int count;
    private double percentage;

    @Override
    public int compareTo(final ArchetypeDescription archetypeDescription) {
        if (count != archetypeDescription.count) {
            return archetypeDescription.count - count;
        }
        return name.compareTo(archetypeDescription.name);
    }
}
