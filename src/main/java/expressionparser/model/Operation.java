package expressionparser.model;

import java.util.List;
import java.util.Map;
import java.util.Optional;

record Operation(String operator, List<Expression> operands) implements Expression {
    private static final float TRUE_VALUE = 1.0f;
    private static final float FALSE_VALUE = 0.0f;

    @Override
    public Optional<Assignment> assignment() {
        if (isAssignmentOperation()) {
            String variableName = ((Atom) operands.get(0)).value();
            Expression valueExpression = operands.get(1);
            return Optional.of(new Assignment(variableName, valueExpression));
        }
        return Optional.empty();
    }

    private boolean isAssignmentOperation() {
        return operator.equals("=")
                && operands.size() == 2
                && operands.getFirst() instanceof Atom;
    }

    @Override
    public float eval(Map<String, Float> variables) {
        return switch (operands.size()) {
            case 1 -> evaluateUnaryOperation(variables);
            case 2 -> evaluateBinaryOperation(variables);
            default -> throw new ParserException(
                    String.format("Invalid number of operands (%d) for operator '%s'", operands.size(), operator)
            );
        };
    }

    private float evaluateUnaryOperation(Map<String, Float> variables) {
        float operandValue = operands.getFirst().eval(variables);
        return switch (operator) {
            case "-" -> -operandValue;
            default -> throw new ParserException(String.format("Unknown unary operator '%s'", operator));
        };
    }

    private float evaluateBinaryOperation(Map<String, Float> variables) {
        float leftOperand = operands.get(0).eval(variables);
        float rightOperand = operands.get(1).eval(variables);
        return switch (operator) {
            case "+" -> leftOperand + rightOperand;
            case "-" -> leftOperand - rightOperand;
            case "*" -> leftOperand * rightOperand;
            case "/" -> performSafeDivision(leftOperand, rightOperand);
            case "==" -> leftOperand == rightOperand ? TRUE_VALUE : FALSE_VALUE;
            case "!=" -> leftOperand != rightOperand ? TRUE_VALUE : FALSE_VALUE;
            case "<"  -> leftOperand <  rightOperand ? TRUE_VALUE : FALSE_VALUE;
            case ">"  -> leftOperand >  rightOperand ? TRUE_VALUE : FALSE_VALUE;
            case "<=" -> leftOperand <= rightOperand ? TRUE_VALUE : FALSE_VALUE;
            case ">=" -> leftOperand >= rightOperand ? TRUE_VALUE : FALSE_VALUE;
            case "=" -> throw new ParserException("Assignment cannot be evaluated directly");
            default -> throw new ParserException(String.format("Unknown binary operator '%s'", operator));
        };
    }

    private float performSafeDivision(float numerator, float denominator) {
        if (denominator == 0) {
            throw new ParserException("Division by zero is not allowed");
        }
        return numerator / denominator;
    }
}