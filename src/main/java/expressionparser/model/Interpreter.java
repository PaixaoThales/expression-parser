package expressionparser.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
        return Result.success(assignment.variable() + " = " + value);
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

        public static Result success(String result) {
            return new Result(result, null);
        }

        public static Result success(float result) {
            return success(String.valueOf(result));
        }

        public static Result failure(Exception exception) {
            return new Result(null, exception);
        }

        public String value() {
            return Optional.ofNullable(value).isPresent() ? value : exception.getMessage();
        }
    }
}
