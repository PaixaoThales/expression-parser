package expressionparser.model;

import java.util.Map;
import java.util.Optional;

public interface Expression {
    static Expression fromString(String input) {
        return Parser.parse(new Lexer(input), 0.0f);
    }

    Optional<Assignment> assignment();

    float eval(Map<String, Float> variables);
}
