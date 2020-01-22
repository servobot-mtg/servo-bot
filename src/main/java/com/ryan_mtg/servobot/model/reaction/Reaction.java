package com.ryan_mtg.servobot.model.reaction;

import java.util.ArrayList;
import java.util.List;

public class Reaction {
    public static final int UNREGISTERED_ID = 0;
    private static ReactionFilter ALWAYS_REACT = new AlwaysReact();

    private int id;
    private String emoteName;
    private boolean secure;
    private List<Pattern> patterns = new ArrayList<>();
    private ReactionFilter filter;

    public Reaction(final int id, final String emoteName, final boolean secure, final Pattern... patterns){
        this(id, emoteName, secure, ALWAYS_REACT, patterns);
    }

    public Reaction(final int id, final String emoteName, final boolean secure, final ReactionFilter filter,
                    final Pattern... patterns){
        this.id = id;
        this.emoteName = emoteName;
        this.secure = secure;
        this.filter = filter;
        for (Pattern pattern : patterns) {
            this.patterns.add(pattern);
        }
    }

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public String getEmoteName() {
        return emoteName;
    }

    public boolean isSecure() {
        return secure;
    }

    public void setSecure(final boolean secure) {
        this.secure = secure;
    }

    public ReactionFilter getFilter() {
        return filter;
    }

    public void addPattern(final Pattern pattern) {
        patterns.add(pattern);
    }

    public List<Pattern> getPatterns() {
        return patterns;
    }

    public boolean matches(final String text) {
        if (!filter.shouldReact()) {
            return false;
        }
        for (Pattern pattern : patterns) {
            if (pattern.matches(text)) {
                return true;
            }
        }
        return false;
    }
}
