package com.example.ui_calculator_app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class CalculatorEngineTest {

    private CalculatorEngine engine;

    @Before
    public void setUp() {
        engine = new CalculatorEngine();
    }

    private void type(String keys) {
        for (char c : keys.toCharArray()) {
            if (Character.isDigit(c)) engine.pressDigit(c);
            else if (c == '.') engine.pressDot();
            else if (c == 'e' || c == 'μ') engine.pressConstant(c);
            else if (c == 's') engine.pressSin();
            else if (c == '=') engine.pressEqual();
            else engine.pressOperator(c);
        }
    }

    private void setDegree(boolean wanted) {
        if (engine.isDegreeMode() != wanted) engine.toggleAngleMode();
    }

    @Test
    public void addsAndShowsResult() {
        type("12+30=");
        assertEquals("=42", engine.getFormattedResult());
        assertEquals(CalculatorEngine.Status.RESULT, engine.getStatus());
    }

    @Test
    public void vietnameseFormat() {
        type("1/2=");
        assertEquals("=0,5", engine.getFormattedResult());
        engine.clear();
        type("1000*1000=");
        assertEquals("=1.000.000", engine.getFormattedResult());
    }

    @Test
    public void divisionByZeroShowsError() {
        type("5/0=");
        assertEquals(CalculatorEngine.Status.ERROR, engine.getStatus());
    }

    @Test
    public void leadingPlusMinusWorkMulDivError() {
        type("+5=");
        assertEquals("=5", engine.getFormattedResult());
        engine.clear();
        type("-5=");
        assertEquals("=-5", engine.getFormattedResult());
        engine.clear();
        type("*5=");
        assertEquals(CalculatorEngine.Status.ERROR, engine.getStatus());
        engine.clear();
        type("/5=");
        assertEquals(CalculatorEngine.Status.ERROR, engine.getStatus());
    }

    @Test
    public void digitAfterResultStartsNewInput() {
        type("2+2=7");
        assertEquals("7", engine.getExpression());
        assertEquals(CalculatorEngine.Status.EDITING, engine.getStatus());
    }

    @Test
    public void operatorAfterResultContinuesFromResult() {
        type("2+2=*3=");
        assertEquals("4*3", engine.getExpression());
        assertEquals("=12", engine.getFormattedResult());
    }

    @Test
    public void consecutiveOperatorsAreReplaced() {
        type("5+*3");
        assertEquals("5*3", engine.getExpression());
    }

    @Test
    public void onlyOneDotPerNumber() {
        type("1.5.2");
        assertEquals("1.52", engine.getExpression());
        type("+.5");
        assertEquals("1.52+0.5", engine.getExpression());
    }

    @Test
    public void deleteRemovesCharAndWholeSin() {
        type("12+");
        engine.pressDelete();
        assertEquals("12", engine.getExpression());
        type("+s");
        assertEquals("12+sin", engine.getExpression());
        engine.pressDelete();
        assertEquals("12+", engine.getExpression());
    }

    @Test
    public void clearResetsEverything() {
        type("2+3=");
        engine.clear();
        assertEquals("", engine.getExpression());
        assertEquals("", engine.getFormattedResult());
        assertFalse(engine.isEditingNonEmpty());
    }

    @Test
    public void editingFlagForTimeout() {
        assertFalse(engine.isEditingNonEmpty());
        type("1");
        assertTrue(engine.isEditingNonEmpty());
        type("=");
        assertFalse(engine.isEditingNonEmpty());
    }

    @Test
    public void keyEAndMu() {
        type("e=");
        assertEquals("=2,7182818285", engine.getFormattedResult());
        engine.clear();
        type("5μ=");
        assertEquals("=0,000005", engine.getFormattedResult());
    }

    @Test
    public void keySinInDegreeAndRadian() {
        setDegree(true);
        type("s30=");
        assertEquals("=0,5", engine.getFormattedResult());
        engine.clear();
        type("s180=");
        assertEquals("=0", engine.getFormattedResult());
        engine.clear();
        setDegree(false);
        type("s0=");
        assertEquals("=0", engine.getFormattedResult());
    }

    @Test
    public void toggleAngleModeRecalculatesResult() {
        setDegree(true);
        type("s1=");
        assertEquals("=0,0174524064", engine.getFormattedResult());
        engine.toggleAngleMode();
        assertEquals("=0,8414709848", engine.getFormattedResult());
        engine.toggleAngleMode();
        assertEquals("=0,0174524064", engine.getFormattedResult());
    }

    @Test
    public void angleModeSurvivesClear() {
        setDegree(true);
        engine.toggleAngleMode();
        engine.clear();
        assertFalse(engine.isDegreeMode());
    }
    @Test
    public void divisionByZeroHasSpecificMessage() {
        type("5/0=");
        assertEquals("Không thể chia cho 0", engine.getErrorMessage());
    }

    @Test
    public void malformedExpressionHasSpecificMessage() {
        type("5+=");
        assertEquals(CalculatorEngine.Status.ERROR, engine.getStatus());
        assertEquals("Biểu thức chưa hợp lệ", engine.getErrorMessage());
    }

    @Test
    public void clearResetsErrorMessage() {
        type("5/0=");
        engine.clear();
        assertEquals("", engine.getErrorMessage());
        assertEquals(CalculatorEngine.Status.EDITING, engine.getStatus());
    }

    @Test
    public void deleteAfterErrorResetsErrorMessage() {
        type("5/0=");
        engine.pressDelete();
        assertEquals("", engine.getErrorMessage());
        assertEquals(CalculatorEngine.Status.EDITING, engine.getStatus());
    }

    @Test
    public void successfulCalculationHasNoErrorMessage() {
        type("5/0=");
        engine.clear();
        type("2+3=");
        assertEquals("", engine.getErrorMessage());
    }

    @Test
    public void restoreBringsBackFullState() {
        type("4+6=");
        String expr = engine.getExpression();
        CalculatorEngine.Status status = engine.getStatus();
        String formatted = engine.getFormattedResult();
        String raw = engine.getRawResult();
        boolean degree = engine.isDegreeMode();

        CalculatorEngine fresh = new CalculatorEngine();
        fresh.restore(expr, status, formatted, raw, "", degree);

        assertEquals("4+6", fresh.getExpression());
        assertEquals("=10", fresh.getFormattedResult());
        assertEquals(CalculatorEngine.Status.RESULT, fresh.getStatus());
        assertEquals(degree, fresh.isDegreeMode());
    }
}