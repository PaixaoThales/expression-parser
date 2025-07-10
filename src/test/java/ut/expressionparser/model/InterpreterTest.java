package ut.expressionparser.model;

import org.junit.Test;
import expressionparser.model.Interpreter;

import static org.junit.Assert.assertEquals;

public class InterpreterTest {

    @Test
    public void returnNumberItself() {
        inputAndExpected("42", "42.0");
    }

    @Test
    public void returnNegativeNumberItself() {
        inputAndExpected("-42", "-42.0");
    }

    @Test
    public void returnNumberItselfWithoutZerosInLeft() {
        inputAndExpected("00000042", "42.0");
    }

    @Test
    public void evaluateExpressionWithSpacesAndTabsAndZerosInLeft() {
        inputAndExpected("   0000000042+10         *9  + 010  - 5+   89+009", "235.0");
    }

    @Test
    public void evaluateSumOperation() {
        inputAndExpected("42 + 10", "52.0");
    }

    @Test
    public void evaluateDecimalSumOperation() {
        inputAndExpected("42.8 + 10.1", "52.9");
    }

    @Test
    public void evaluateSumZeroOperation() {
        inputAndExpected("42 + 0", "42.0");
    }

    @Test
    public void evaluateMinusOperation() {
        inputAndExpected("42 - 10", "32.0");
    }

    @Test
    public void evaluateNegativeNumberOperation() {
        inputAndExpected("-42 + 10", "-32.0");
    }

    @Test
    public void evaluateMultiNegativeNumberOperation() {
        inputAndExpected("- ------42 + 10", "-32.0");
    }

    @Test
    public void evaluateComplexNegativeNumberOperation() {
        inputAndExpected(" -  (-42 + -(-10) * -2 + ( - 20))", "82.0");
    }

    @Test
    public void evaluateDecimalMinusOperation() {
        inputAndExpected("42.981 - 12.10", "30.880999");
    }

    @Test
    public void evaluateMinusOperationWithNegativeResult() {
        inputAndExpected("9 - 10", "-1.0");
    }

    @Test
    public void evaluateMultiplicationOperation() {
        inputAndExpected("42 * 10", "420.0");
    }

    @Test
    public void evaluateDecimalMultiplicationOperation() {
        inputAndExpected("42.42 * 10.10", "428.442");
    }

    @Test
    public void evaluateIntegerDivisionOperation() {
        inputAndExpected("420 / 10", "42.0");
    }

    @Test
    public void evaluateDecimalDivisionOperation() {
        inputAndExpected("42 / 10", "4.2");
    }

    @Test
    public void evaluateZeroDivisionOperation() {
        inputAndExpected("42 / 0", "Division by zero is not allowed");
    }

    @Test
    public void assignmentVariable() {
        inputAndExpected("a = 89", "a = 89.0");
    }

    @Test
    public void assignmentNegativeVariable() {
        inputAndExpected("a = -903 + 19 * -2", "a = -941.0");
    }

    @Test
    public void assignmentVariableWithLongName() {
        inputAndExpected("abc25 = 1098", "abc25 = 1098.0");
    }

    @Test
    public void complexAssignmentVariable() {
        Interpreter interpreter = new Interpreter();

        interpreter.execute("a = 89");
        interpreter.execute("b = 11");
        Interpreter.Result result = interpreter.execute("abc = a + 94 * 54 / 32 - 78 + b");

        assertEquals("abc = 180.625", result.value());
    }

    @Test
    public void evaluateExpressionWithVariable() {
        Interpreter interpreter = new Interpreter();

        interpreter.execute("a = 76");
        interpreter.execute("b = 13");
        interpreter.execute("x = 29");
        Interpreter.Result result = interpreter.execute("a * b + a - ((62 + x) + 12 / a)");

        assertEquals("972.8421", result.value());
    }

    @Test
    public void reassignmentVariable() {
        Interpreter interpreter = new Interpreter();

        Interpreter.Result assignment = interpreter.execute("y = 67");

        assertEquals("y = 67.0", assignment.value());

        Interpreter.Result reassignment = interpreter.execute("y = 98725");

        assertEquals("y = 98725.0", reassignment.value());
    }

    @Test
    public void returnExceptionMessageWhenVariableIsNonexistent() {
        inputAndExpected("a + 2", "Undefined variable 'a'");
    }

    @Test
    public void resolveBracketExpression() {
        inputAndExpected("(75 + 27)", "102.0");
    }

    @Test
    public void resolveComplexBracketInExpression() {
        inputAndExpected("((32 + 47) * (26 - (11 + 11))) / 5", "63.2");
    }

    @Test
    public void resolveBracketWithComplexExpression() {
        inputAndExpected("11 / 90 - (75 + 47) + 10", "-111.87778");
    }

    @Test
    public void resolveComplexExpression() {
        inputAndExpected("10 / 90 - 75 + 27 * 42", "1059.1111");
    }

    @Test
    public void returnExceptionMessageWhenBracketIsNotClosed() {
        inputAndExpected("((75 + 27)", "Expected ')', but reached end of input");
    }

    @Test
    public void returnExceptionMessageWhenBracketIsNotOpened() {
        inputAndExpected("(75 + 27))", "Unexpected ')'");
    }

    @Test
    public void returnExceptionMessageWhenExpressionHasInvalidStart() {
        inputAndExpected("&10 - 98", "Invalid start of expression: &");
    }

    @Test
    public void returnExceptionMessageWhenExpressionExpectedOperator() {
        inputAndExpected("10x - 98", "Expected operator, but found: x");
    }

    @Test
    public void returnExceptionMessageWhenExpressionHasInvalidOperator() {
        inputAndExpected("10 & 98", "Unknown operator: '&'");
    }

    private void inputAndExpected(String input, String expected) {
        Interpreter interpreter = new Interpreter();

        Interpreter.Result result = interpreter.execute(input);

        assertEquals(expected, result.value());
    }
}
