package expressionparser.model;

import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

public record Atom(String value) implements Expression {
    private static final Pattern NUMBER_PATTERN = Pattern.compile("\\d+(\\.\\d+)?");
    private static final Pattern VARIABLE_PATTERN = Pattern.compile("([A-Za-z][A-Za-z0-9]*)");

    @Override
    public Optional<Assignment> assignment() {
        return Optional.empty();
    }

    @Override
    public float eval(Map<String, Float> variables) {
        if (isNumber()) {
            return resolveNumber();
        } else if (isVariable()) {
            return resolveVariable(variables);
        }
        throw new ParserException(String.format("Invalid atom value '%s'", value));
    }

    private boolean isNumber() {
        return NUMBER_PATTERN.matcher(value).matches();
    }

    private float resolveNumber() {
        return Float.parseFloat(value);
    }

    private boolean isVariable() {
        return VARIABLE_PATTERN.matcher(value).matches();
    }

    private float resolveVariable(Map<String, Float> variables) {
        if (!variables.containsKey(value)) {
            throw new ParserException(String.format("Undefined variable '%s'", value));
        }
        return variables.get(value);
    }
}
