package com.ryan_mtg.servobot.model.parser;

public class Lexer {
    private String input;
    private int position;
    private Token nextToken;

    public Lexer(final String input) {
        this.input = input;
        position = 0;
        readToken();
    }

    public boolean hasNextToken() {
        return nextToken != null;
    }

    public Token peekNextToken() {
        return nextToken;
    }

    public Token getNextToken() {
        Token result = nextToken;
        readToken();
        return result;
    }

    public boolean isNextToken(final Token.Type tokenType) {
        return nextToken != null && nextToken.getType() == tokenType;
    }

    private void readToken() {
        nextToken = null;
        while (position < input.length() && Character.isWhitespace(input.charAt(position))) {
            position++;
        }

        if (position >= input.length()) {
            return;
        }

        char startCharacter = input.charAt(position);
        switch (startCharacter) {
            case '(':
                nextToken = new Token(Token.Type.OPEN_PARENTHESIS, "(");
                position++;
                return;
            case ')':
                nextToken = new Token(Token.Type.CLOSE_PARENTHESIS, ")");
                position++;
                return;
            case '*':
                nextToken = new Token(Token.Type.MULTIPLY, "*");
                position++;
                return;
            case '?':
                nextToken = new Token(Token.Type.CONDITIONAL, "?");
                position++;
                return;
            case ':':
                nextToken = new Token(Token.Type.CONDITIONAL_ELSE, ":");
                position++;
                return;
            case '.':
                nextToken = new Token(Token.Type.MEMBER_ACCESSOR, ".");
                position++;
                return;
            case '+':
                if (position + 1 < input.length() && input.charAt(position + 1) == '+') {
                    nextToken = new Token(Token.Type.INCREMENT, "++");
                    position+=2;
                    return;
                }
                nextToken = new Token(Token.Type.ADD, "+");
                position++;
                return;
        }

        int start = position;

        if (Character.isDigit(startCharacter)) {
            while(position < input.length() && Character.isDigit(input.charAt(position))) {
                position++;
            }
            nextToken = new Token(Token.Type.INTEGER, input.substring(start, position));
            return;
        }

        if (Character.isJavaIdentifierStart(startCharacter)) {
            while(position < input.length() && Character.isJavaIdentifierPart(input.charAt(position))) {
                position++;
            }
            nextToken = new Token(Token.Type.IDENTIFIER, input.substring(start, position));
        }
    }
}
