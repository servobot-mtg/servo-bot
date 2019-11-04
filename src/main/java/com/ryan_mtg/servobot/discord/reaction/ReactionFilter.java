package com.ryan_mtg.servobot.discord.reaction;

import java.util.regex.Pattern;

public interface ReactionFilter {
    Pattern createPattern(String patternString);
    boolean shouldReact();
    int getType();
}
