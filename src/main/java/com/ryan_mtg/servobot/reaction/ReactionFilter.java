package com.ryan_mtg.servobot.reaction;

import java.util.regex.Pattern;

public interface ReactionFilter {
    int getType();
    void setTimeZone(String timeZone);
    Pattern createPattern(String patternString);
    boolean shouldReact();
}
