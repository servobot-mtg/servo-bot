package com.ryan_mtg.servobot.discord.reaction;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Reaction {
    public static final int UNREGISTERED_ID = 0;
    private static ReactionFilter ALWAYS_REACT = new AlwaysReact();

    private int id;
    private String emoteName;
    private List<Pattern> patterns = new ArrayList<>();
    private List<String> patternStrings = new ArrayList<>();
    private ReactionFilter filter;

    public Reaction(final int id, final String emoteName, final String... patternStrings){
        this(id, emoteName, ALWAYS_REACT, patternStrings);
    }

    public Reaction(final int id, final String emoteName, final ReactionFilter filter, final String... patternStrings){
        this.id = id;
        this.emoteName = emoteName;
        this.filter = filter;
        for (String patternString : patternStrings) {
            addPattern(patternString);
        }
    }

    public int getId() {
        return id;
    }

    public String getEmoteName() {
        return emoteName;
    }

    public ReactionFilter getFilter() {
        return filter;
    }

    public List<String> getPatterns() {
        return patternStrings;
    }

    public void addPattern(final String patternString) {
        patterns.add(filter.createPattern(patternString));
        patternStrings.add(patternString);
    }

    public boolean matches(final String text) {
        if (!filter.shouldReact()) {
            return false;
        }
        for (Pattern pattern : patterns) {
            if (pattern.matcher(text).find()) {
                return true;
            }
        }
        return false;
    }
}
