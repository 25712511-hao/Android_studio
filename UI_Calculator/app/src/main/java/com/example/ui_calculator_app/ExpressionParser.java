package com.example.ui_calculator_app;

public final class ExpressionParser {

    public static final char CHAR_E = 'e';
    public static final char CHAR_MU = 'μ';
    public static final String FUNC_SIN = "sin";

    public static final double VALUE_E = Math.E;
    public static final double VALUE_MU = 1e-6;

    private final String source;
    private final boolean degreeMode;
    private int pos = 0;

    private ExpressionParser(String source, boolean degreeMode) {
        this.source = source;
        this.degreeMode = degreeMode;
    }

    public static double evaluate(String expression, boolean degreeMode) {
        ExpressionParser parser = new ExpressionParser(expression, degreeMode);
        double value = parser.parseExpression();
        if (parser.pos != expression.length()) {
            throw new NumberFormatException("Ký tự không hợp lệ: " + expression.charAt(parser.pos));
        }
        return value;
    }

    private double parseExpression() {
        double value = parseTerm();
        while (peekIs('+') || peekIs('-')) {
            char op = source.charAt(pos++);
            double right = parseTerm();
            value = (op == '+') ? value + right : value - right;
        }
        return value;
    }

    private double parseTerm() {
        double value = parseUnary();
        while (true) {
            if (peekIs('*')) {
                pos++;
                value *= parseUnary();
            } else if (peekIs('/')) {
                pos++;
                double divisor = parseUnary();
                if (divisor == 0) throw new ArithmeticException("Chia cho 0");
                value /= divisor;
            } else if (startsOperand()) {
                value *= parseUnary();
            } else {
                return value;
            }
        }
    }

    private double parseUnary() {
        if (peekIs('-')) { pos++; return -parseUnary(); }
        if (peekIs('+')) { pos++; return parseUnary(); }
        return parsePrimary();
    }

    private double parsePrimary() {
        if (peekIs(CHAR_E)) { pos++; return VALUE_E; }
        if (peekIs(CHAR_MU)) { pos++; return VALUE_MU; }
        if (source.startsWith(FUNC_SIN, pos)) {
            pos += FUNC_SIN.length();
            double angle = parseUnary();
            return Math.sin(degreeMode ? Math.toRadians(angle) : angle);
        }
        return parseNumber();
    }

    private double parseNumber() {
        int start = pos;
        while (pos < source.length()
                && (Character.isDigit(source.charAt(pos)) || source.charAt(pos) == '.')) {
            pos++;
        }
        if (start == pos) throw new NumberFormatException("Thiếu toán hạng");
        return Double.parseDouble(source.substring(start, pos));
    }

    private boolean startsOperand() {
        if (pos >= source.length()) return false;
        char c = source.charAt(pos);
        return Character.isDigit(c) || c == '.' || c == CHAR_E || c == CHAR_MU
                || source.startsWith(FUNC_SIN, pos);
    }

    private boolean peekIs(char c) {
        return pos < source.length() && source.charAt(pos) == c;
    }

    public static boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }
}
