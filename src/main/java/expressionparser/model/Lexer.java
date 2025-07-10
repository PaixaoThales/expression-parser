package expressionparser.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Lexer {

    private final List<Token> tokens;

    public Lexer(String input) {
        this.tokens = new ArrayList<>();
        tokenize(input);
        Collections.reverse(tokens);
    }

    private void tokenize(String input) {
        int position = 0;
        while (position < input.length()) {
            char currentChar = input.charAt(position);

            if (Character.isWhitespace(currentChar)) {
                position++;
                continue;
            }

            if (isStartOfNumber(input, position)) {
                position = consumeNumber(input, position);
                continue;
            }

            if (Character.isLetter(currentChar)) {
                position = consumeIdentifier(input, position);
                continue;
            }

            position = consumeOperator(currentChar, position);
        }
    }

    private boolean isStartOfNumber(String input, int position) {
        char currentChar = input.charAt(position);
        return Character.isDigit(currentChar) ||
                (currentChar == '.' && position + 1 < input.length() && Character.isDigit(input.charAt(position + 1)));
    }

    private int consumeNumber(String input, int startPosition) {
        StringBuilder numberBuilder = new StringBuilder();
        boolean hasDecimalPoint = false;
        int position = startPosition;

        while (position < input.length()) {
            char c = input.charAt(position);
            if (Character.isDigit(c)) {
                numberBuilder.append(c);
            } else if (c == '.' && !hasDecimalPoint) {
                hasDecimalPoint = true;
                numberBuilder.append('.');
            } else {
                break;
            }
            position++;
        }

        tokens.add(new Token(Token.TokenType.ATOM, numberBuilder.toString()));
        return position;
    }

    private int consumeIdentifier(String input, int startPosition) {
        StringBuilder identifierBuilder = new StringBuilder();
        int position = startPosition;

        while (position < input.length() && Character.isLetterOrDigit(input.charAt(position))) {
            identifierBuilder.append(input.charAt(position));
            position++;
        }

        tokens.add(new Token(Token.TokenType.ATOM, identifierBuilder.toString()));
        return position;
    }

    private int consumeOperator(char operatorChar, int position) {
        tokens.add(new Token(Token.TokenType.OPERATOR, String.valueOf(operatorChar)));
        return position + 1;
    }

    public Token next() {
        return tokens.isEmpty() ? Token.EOF : tokens.removeLast();
    }

    public Token peek() {
        return tokens.isEmpty() ? Token.EOF : tokens.getLast();
    }
}
