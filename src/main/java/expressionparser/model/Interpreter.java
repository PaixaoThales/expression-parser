package expressionparser.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Interpreter {
    private final Map<String, Float> variables;

    public Interpreter() {
        this.variables = new HashMap<>();
    }

    public Result execute(String input) {
        try {
            Expression expression = Expression.fromString(input);
            return expression.assignment()
                    .map(this::handleAssignment)
                    .orElseGet(() -> evalExpression(expression));
        } catch (ParserException exception) {
            return Result.failure(exception);
        } catch (Exception exception) {
            return Result.failure(new ParserException("Unknown exception: " + exception.getMessage()));
        }
    }

    private Result handleAssignment(Assignment assignment) {
        float value = assignment.expression().eval(variables);
        variables.put(assignment.variable(), value);
        return Result.success(assignment.variable(), value);
    }

    private Result evalExpression(Expression expression) {
        return Result.success(expression.eval(variables));
    }

    public static class Result {
        private final String value;
        private final Exception exception;

        private Result(String value, Exception exception) {
            this.value = value;
            this.exception = exception;
        }

        public static Result success(String variable, float result) {
            return new Result(variable + " = " + formatResult(result), null);
        }

        public static Result success(float result) {
            return new Result(formatResult(result), null);
        }

        public static Result failure(Exception exception) {
            return new Result(null, exception);
        }

        private static String formatResult(float value) {
            return Math.floor(value) == value ? String.valueOf((int) value) : String.valueOf(value);
        }

        public String value() {
            return Objects.nonNull(value) ? value : exception.getMessage();
        }
    }
}
