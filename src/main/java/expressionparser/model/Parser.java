package expressionparser.model;

import java.util.List;

public class Parser {

    public static Expression parse(Lexer lexer, float minPrecedence) {
        return parseExpression(lexer, minPrecedence, false);
    }

    private static Expression parseExpression(Lexer lexer, float minPrecedence, boolean inParentheses) {
        Expression left = parsePrimary(lexer, inParentheses);
        while (true) {
            Token lookahead = lexer.peek();
            if (shouldStopParsing(lookahead, inParentheses)) {
                break;
            }
            if (lookahead.type() != Token.TokenType.OPERATOR) {
                throw new ParserException("Expected operator, but found: " + lookahead.value());
            }
            PrecedenceRange precedenceRange = operatorPrecedence(lookahead.value());
            if (precedenceRange.left < minPrecedence) {
                break;
            }
            lexer.next();
            Expression right = parseExpression(lexer, precedenceRange.right, inParentheses);
            left = new Operation(lookahead.value(), List.of(left, right));
        }

        return left;
    }

    private static Expression parsePrimary(Lexer lexer, boolean inParentheses) {
        Token nextToken = lexer.next();
        return switch (nextToken) {
            case Token token when token.type() == Token.TokenType.ATOM -> new Atom(token.value());

            case Token token when token.type() == Token.TokenType.OPERATOR && token.value().equals("-") -> {
                Expression operand = parseExpression(lexer, 3.0f, inParentheses);
                yield new Operation("-", List.of(operand));
            }

            case Token token when token.type() == Token.TokenType.OPERATOR && token.value().equals("(") -> {
                Expression inner = parseExpression(lexer, 0.0f, true);
                expectClosingParenthesis(lexer);
                yield inner;
            }

            default -> throw new ParserException("Invalid start of expression: " + nextToken.value());
        };
    }

    private static boolean shouldStopParsing(Token token, boolean inParentheses) {
        if (token == Token.EOF) {
            if (inParentheses) {
                throw new ParserException("Expected ')', but reached end of input");
            }
            return true;
        }

        if (token.type() == Token.TokenType.OPERATOR && token.value().equals(")")) {
            if (!inParentheses) {
                throw new ParserException("Unexpected ')'");
            }
            return true;
        }

        return false;
    }

    private static void expectClosingParenthesis(Lexer lexer) {
        Token closing = lexer.next();
        if (closing.type() != Token.TokenType.OPERATOR || !closing.value().equals(")")) {
            throw new ParserException("Expected ')', but found: " + closing.value());
        }
    }

    private static PrecedenceRange operatorPrecedence(String operator) {
        return switch (operator) {
            case "=" -> new PrecedenceRange(0.2f, 0.1f);
            case "+", "-" -> new PrecedenceRange(1.0f, 1.1f);
            case "*", "/" -> new PrecedenceRange(2.0f, 2.1f);
            default -> throw new ParserException("Unknown operator: '" + operator + "'");
        };
    }

    private record PrecedenceRange(float left, float right) {
    }
}