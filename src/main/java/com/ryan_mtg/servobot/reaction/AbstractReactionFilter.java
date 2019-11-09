package com.ryan_mtg.servobot.reaction;

import java.util.regex.Pattern;

public abstract class AbstractReactionFilter implements ReactionFilter {
    @Override
    public Pattern createPattern(final String patternString) {
        String word = patternString;

        if (word.charAt(0) == '@') {
            word = word.substring(1);
            word = "\\b" + word + "\\b";
        }

        return Pattern.compile(word, Pattern.CASE_INSENSITIVE);
    }
}
