package com.ryan_mtg.servobot.model.reaction;

import com.ryan_mtg.servobot.data.models.ReactionPatternRow;
import com.ryan_mtg.servobot.events.BotErrorException;

public class Pattern {
    public static final int UNREGISTERED_ID = 0;
    private static final int MAX_PATTERN_SIZE = ReactionPatternRow.MAX_PATTERN_SIZE;

    private int id;
    private String patternString;
    private java.util.regex.Pattern pattern;

    public Pattern(final int id, final String patternString) throws BotErrorException {
        this.id = id;
        this.patternString = patternString;
        this.pattern = createPattern(patternString);

        if (patternString.length() > MAX_PATTERN_SIZE) {
            throw new BotErrorException(
                    String.format("Pattern too long (max %d): %s", MAX_PATTERN_SIZE, patternString));
        }
    }

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public String getPatternString() {
        return patternString;
    }

    public boolean matches(final String text) {
        return pattern.matcher(text).find();
    }

    private java.util.regex.Pattern createPattern(final String patternString) {
        String word = patternString;

        switch (word.charAt(0)) {
            case '!':
            case '<':
                word = "\\b" + word.substring(1);
                break;
            case '>':
                word = word.substring(1) + "\\b";
                break;
            case '@':
                word = "\\b" + word.substring(1) + "\\b";
                break;
        }

        return java.util.regex.Pattern.compile(word, java.util.regex.Pattern.CASE_INSENSITIVE);
    }
}
