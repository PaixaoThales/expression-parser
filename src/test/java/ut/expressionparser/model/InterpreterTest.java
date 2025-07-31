package ut.expressionparser.model;

import expressionparser.model.Interpreter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InterpreterTest {

    @Test
    void returnNumberItself() {
        inputAndExpected("42", "42");
    }

    @Test
    void returnNegativeNumberItself() {
        inputAndExpected("-42", "-42");
    }

    @Test
    void returnNumberItselfWithoutZerosInLeft() {
        inputAndExpected("00000042", "42");
    }

    @Test
    void evaluateExpressionWithSpacesAndTabsAndZerosInLeft() {
        inputAndExpected("   0000000042+10         *9  + 010  - 5+   89+009", "235");
    }

    @Test
    void evaluateSumOperation() {
        inputAndExpected("42 + 10", "52");
    }

    @Test
    void evaluatePrecisionSum() {
        inputAndExpected("0.1 + 0.2", "0.3");
    }

    @Test
    void evaluateDecimalSumOperation() {
        inputAndExpected("42.8 + 10.1", "52.9");
    }

    @Test
    void evaluateSumZeroOperation() {
        inputAndExpected("42 + 0", "42");
    }

    @Test
    void evaluateMinusOperation() {
        inputAndExpected("42 - 10", "32");
    }

    @Test
    void evaluateNegativeNumberOperation() {
        inputAndExpected("-42 + 10", "-32");
    }

    @Test
    void evaluateMultiNegativeNumberOperation() {
        inputAndExpected("- ------42 + 10", "-32");
    }

    @Test
    void evaluateComplexNegativeNumberOperation() {
        inputAndExpected(" -  (-42 + -(-10) * -2 + ( - 20))", "82");
    }

    @Test
    void evaluateDecimalMinusOperation() {
        inputAndExpected("42.981 - 12.10", "30.880999");
    }

    @Test
    void evaluateMinusOperationWithNegativeResult() {
        inputAndExpected("9 - 10", "-1");
    }

    @Test
    void evaluateMultiplicationOperation() {
        inputAndExpected("42 * 10", "420");
    }

    @Test
    void evaluateDecimalMultiplicationOperation() {
        inputAndExpected("42.42 * 10.10", "428.442");
    }

    @Test
    void evaluateIntegerDivisionOperation() {
        inputAndExpected("420 / 10", "42");
    }

    @Test
    void evaluateDecimalDivisionOperation() {
        inputAndExpected("42 / 10", "4.2");
    }

    @Test
    void evaluateZeroDivisionOperation() {
        inputAndExpected("42 / 0", "Division by zero is not allowed");
    }

    @Test
    void assignmentVariable() {
        inputAndExpected("a = 89", "a = 89");
    }

    @Test
    void assignmentNegativeVariable() {
        inputAndExpected("a = -903.23 + 19 * -2", "a = -941.23");
    }

    @Test
    void assignmentVariableWithLongName() {
        inputAndExpected("abc25 = 1098", "abc25 = 1098");
    }

    @Test
    void complexAssignmentVariable() {
        Interpreter interpreter = new Interpreter();

        Interpreter.Result resultA = interpreter.execute("a = 89");
        Interpreter.Result resultB = interpreter.execute("b = 11");
        Interpreter.Result finalResult = interpreter.execute("abc = a + 94 * 54 / 32 - 78 + b - (9 >= 10) + (9 == 9)");

        assertEquals("a = 89", resultA.value());
        assertEquals("b = 11", resultB.value());
        assertEquals("abc = 181.625", finalResult.value());
    }

    @Test
    void evaluateExpressionWithVariable() {
        Interpreter interpreter = new Interpreter();

        Interpreter.Result resultA = interpreter.execute("a = 76");
        Interpreter.Result resultB = interpreter.execute("b = 13");
        Interpreter.Result resultX = interpreter.execute("x = 29");
        Interpreter.Result finalResult = interpreter.execute("a * b + a - ((62 + x) + 12 / a)");

        assertEquals("a = 76", resultA.value());
        assertEquals("b = 13", resultB.value());
        assertEquals("x = 29", resultX.value());
        assertEquals("972.8421", finalResult.value());
    }

    @Test
    void reassignmentVariable() {
        Interpreter interpreter = new Interpreter();

        Interpreter.Result assignment = interpreter.execute("y = 67");

        assertEquals("y = 67", assignment.value());

        Interpreter.Result reassignment = interpreter.execute("y = 98725");

        assertEquals("y = 98725", reassignment.value());
    }

    @Test
    void returnExceptionMessageWhenVariableIsNonexistent() {
        inputAndExpected("a + 2", "Undefined variable 'a'");
    }

    @Test
    void resolveBracketExpression() {
        inputAndExpected("(75 + 27)", "102");
    }

    @Test
    void resolveComplexBracketInExpression() {
        inputAndExpected("((32 + 47) * (26 - (11 + 11))) / 5 - ((9 < 10) + 2)", "60.2");
    }

    @Test
    void resolveBracketWithComplexExpression() {
        inputAndExpected("11 / 90 - (75 + 47) + 10", "-111.87778");
    }

    @Test
    void resolveComplexExpression() {
        inputAndExpected("10 / 90 - 75 + 27 * 42", "1059.1111");
    }

    @Test
    void returnExceptionMessageWhenBracketIsNotClosed() {
        inputAndExpected("((75 + 27)", "Expected ')', but reached end of input");
    }

    @Test
    void returnExceptionMessageWhenBracketIsNotOpened() {
        inputAndExpected("(75 + 27))", "Unexpected ')'");
    }

    @Test
    void returnExceptionMessageWhenExpressionHasInvalidStart() {
        inputAndExpected("&10 - 98", "Invalid start of expression: &");
    }

    @Test
    void returnExceptionMessageWhenExpressionExpectedOperator() {
        inputAndExpected("10x - 98", "Expected operator, but found: x");
    }

    @Test
    void returnExceptionMessageWhenExpressionHasInvalidOperator() {
        inputAndExpected("10 & 98", "Unknown operator: '&'");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "2 > 1",
            "5 >= 5",
            " 6 >= 5 ",
            "3 != 4",
            "1 < 2",
            "10 <= 10",
            " 9   <=   10  ",
            "3 == 3"
    })
    void returnOneWhenExpressionIsTrue(String input) {
        inputAndExpected(input, "1");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "1 > 2",
            "4 >= 5",
            "4 != 4",
            "2 < 1",
            "10 <= 9",
            "4 == 3"
    })
    void returnZeroWhenExpressionIsFalse(String input) {
        inputAndExpected(input, "0");
    }

    @Test
    void resolveLogicalExpression() {
        inputAndExpected("3==6/2", "1");
    }

    private void inputAndExpected(String input, String expected) {
        Interpreter interpreter = new Interpreter();

        Interpreter.Result result = interpreter.execute(input);

        assertEquals(expected, result.value());
    }
}
