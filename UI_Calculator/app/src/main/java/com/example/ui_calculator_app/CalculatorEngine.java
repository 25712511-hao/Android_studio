package com.example.ui_calculator_app;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class CalculatorEngine {

    public enum Status { EDITING, RESULT, ERROR }

    private static final String DECIMAL_PATTERN = "#,##0.##########";
    private static final int RESULT_SCALE = 12;

    private final StringBuilder expression = new StringBuilder();
    private Status status = Status.EDITING;
    private String formattedResult = "";
    private String errorMessage = "";
    private String rawResult = "";
    private boolean degreeMode = false;

    public String getErrorMessage() { return errorMessage; }
    public String getExpression() { return expression.toString(); }
    public String getFormattedResult() { return formattedResult; }
    public Status getStatus() { return status; }
    public String getRawResult() { return rawResult; }
    public boolean isDegreeMode() { return degreeMode; }

    public boolean isEditingNonEmpty() {
        return status == Status.EDITING && expression.length() > 0;
    }
    public void pressDigit(char digit) {
        startNewInputIfFinished();
        expression.append(digit);
    }

    public void pressDot() {
        startNewInputIfFinished();
        if (currentNumberHasDot()) return;
        if (expression.length() == 0 || !endsWithNumberChar()) {
            expression.append('0');
        }
        expression.append('.');
    }

    public void pressOperator(char op) {
        if (status == Status.RESULT) {
            String carry = rawResult;
            clear();
            expression.append(carry);
        } else if (status == Status.ERROR) {
            clear();
        }

        if (expression.length() == 0) {
            expression.append(op);
            return;
        }
        char last = lastChar();
        if (ExpressionParser.isOperator(last)) {
            expression.setCharAt(expression.length() - 1, op);
            return;
        }
        if (endsWithFunction() && op != '-') return;
        if (last == '.') expression.append('0');
        expression.append(op);
    }

    public void pressConstant(char constant) {
        startNewInputIfFinished();
        expression.append(constant);
    }

    public void pressSin() {
        startNewInputIfFinished();
        expression.append(ExpressionParser.FUNC_SIN);
    }

    public void toggleAngleMode() {
        degreeMode = !degreeMode;
        if (status == Status.RESULT) {
            calculate();
        }
    }

    public void pressDelete() {
        if (status != Status.EDITING) {
            status = Status.EDITING;
            formattedResult = "";
            errorMessage = "";
        }
        if (endsWithFunction()) {
            expression.setLength(expression.length() - ExpressionParser.FUNC_SIN.length());
        } else if (expression.length() > 0) {
            expression.deleteCharAt(expression.length() - 1);
        }
    }

    public void restore(String savedExpression, Status savedStatus, String savedFormatted,
                        String savedRaw, String savedError, boolean savedDegreeMode) {
        expression.setLength(0);
        expression.append(savedExpression);
        status = savedStatus;
        formattedResult = savedFormatted;
        rawResult = savedRaw;
        errorMessage = savedError;
        degreeMode = savedDegreeMode;
    }

    public void clear() {
        expression.setLength(0);
        formattedResult = "";
        rawResult = "";
        status = Status.EDITING;
        errorMessage = "";
    }

    public void pressEqual() {
        if (expression.length() == 0 || status != Status.EDITING) return;
        calculate();
    }

    private void calculate() {
        try {
            double value = ExpressionParser.evaluate(expression.toString(), degreeMode);
            if (Double.isNaN(value) || Double.isInfinite(value)) {
                setError("Kết quả không hợp lệ");
                return;
            }
            BigDecimal rounded = BigDecimal.valueOf(value).setScale(RESULT_SCALE, RoundingMode.HALF_UP);
            rawResult = rounded.stripTrailingZeros().toPlainString();
            formattedResult = "=" + formatter().format(rounded);
            errorMessage = "";
            status = Status.RESULT;
        } catch (ArithmeticException e) {
            setError("Không thể chia cho 0");
        } catch (NumberFormatException e) {
            setError("Biểu thức chưa hợp lệ");
        }
    }

    private void setError(String message) {
        formattedResult = "";
        rawResult = "";
        errorMessage = message;
        status = Status.ERROR;
    }

    private void startNewInputIfFinished() {
        if (status != Status.EDITING) clear();
    }

    private static DecimalFormat formatter() {
        return new DecimalFormat(DECIMAL_PATTERN, new DecimalFormatSymbols(Locale.forLanguageTag("vi-VN")));
    }

    private char lastChar() {
        return expression.charAt(expression.length() - 1);
    }

    private boolean endsWithNumberChar() {
        char c = lastChar();
        return Character.isDigit(c) || c == '.';
    }

    private boolean endsWithFunction() {
        int len = expression.length();
        int fl = ExpressionParser.FUNC_SIN.length();
        return len >= fl && expression.substring(len - fl).equals(ExpressionParser.FUNC_SIN);
    }

    private boolean currentNumberHasDot() {
        for (int i = expression.length() - 1; i >= 0; i--) {
            char c = expression.charAt(i);
            if (c == '.') return true;
            if (!Character.isDigit(c)) return false;
        }
        return false;
    }
}