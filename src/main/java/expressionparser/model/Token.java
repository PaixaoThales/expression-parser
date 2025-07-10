package expressionparser.model;

public class Token {
    public static final Token EOF = new Token(TokenType.EOF, "\0");
    private final TokenType type;
    private final String value;

    public Token(TokenType type, String value) {
        this.type = type;
        this.value = value;
    }

    public TokenType type() {
        return type;
    }

    public String value() {
        return value;
    }

    public enum TokenType {
        ATOM,
        OPERATOR,
        EOF
    }
}
