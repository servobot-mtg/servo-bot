package com.ryan_mtg.servobot.model.reaction;

public class Pattern {
    public static final int UNREGISTERED_ID = 0;
    private int id;
    private String patternString;
    private java.util.regex.Pattern pattern;

    public Pattern(final int id, final String patternString) {
        this.id = id;
        this.patternString = patternString;
        this.pattern = createPattern(patternString);
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
