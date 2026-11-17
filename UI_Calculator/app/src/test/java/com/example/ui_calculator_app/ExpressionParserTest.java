package com.example.ui_calculator_app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ExpressionParserTest {

    private static final double DELTA = 1e-9;

    private static double calc(String expr) {
        return ExpressionParser.evaluate(expr, true);
    }

    @Test
    public void basicOperations() {
        assertEquals(5, calc("2+3"), DELTA);
        assertEquals(-1, calc("2-3"), DELTA);
        assertEquals(6, calc("2*3"), DELTA);
        assertEquals(2.5, calc("5/2"), DELTA);
    }

    @Test
    public void precedence() {
        assertEquals(14, calc("2+3*4"), DELTA);
        assertEquals(1, calc("10-6/2*3"), DELTA);
    }

    @Test
    public void decimalAndSignedNumbers() {
        assertEquals(0.3, calc("0.1+0.2"), DELTA);
        assertEquals(5, calc("+5"), DELTA);
        assertEquals(-3, calc("-5+2"), DELTA);
        assertEquals(7, calc("5--2"), DELTA);
    }

    @Test
    public void divisionByZeroThrows() {
        assertThrows(ArithmeticException.class, () -> calc("5/0"));
    }

    @Test
    public void malformedThrows() {
        assertThrows(NumberFormatException.class, () -> calc("5+"));
        assertThrows(NumberFormatException.class, () -> calc("*5"));
        assertThrows(NumberFormatException.class, () -> calc("/5"));
        assertThrows(NumberFormatException.class, () -> calc("1.2.3"));
        assertThrows(NumberFormatException.class, () -> calc("sin"));
    }

    @Test
    public void constants() {
        assertEquals(Math.E, calc("e"), DELTA);
        assertEquals(2 * Math.E, calc("2e"), DELTA);
        assertEquals(5e-6, calc("5μ"), 1e-15);
    }

    @Test
    public void sinDegreeAndRadian() {
        assertEquals(0.5, calc("sin30"), DELTA);
        assertEquals(1.5, calc("1+sin30"), DELTA);
        assertEquals(Math.sin(1), ExpressionParser.evaluate("sin1", false), DELTA);
    }

    @Test
    public void isOperator() {
        assertTrue(ExpressionParser.isOperator('+'));
        assertTrue(ExpressionParser.isOperator('/'));
        assertFalse(ExpressionParser.isOperator('5'));
    }
}