package com.example.ui_calculator_app;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final long TIMEOUT_MS = 10_000L;
    private static final int PROGRESS_MAX = 1000;

    private TextView txtExpression;
    private TextView txtResult;
    private TextView txtPercent;
    private ProgressBar pbTimeout;

    private final StringBuilder expression = new StringBuilder();
    private boolean justCalculated = false;
    private ValueAnimator timeoutAnimator;
    private boolean animatorCancelled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, windowInsets) -> {
            Insets bars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return windowInsets;
        });

        txtExpression = findViewById(R.id.txtExpression);
        txtResult = findViewById(R.id.txtResult);
        txtPercent = findViewById(R.id.txtPercent);
        pbTimeout = findViewById(R.id.pbTimeout);

        int[] keyIds = {
                R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
                R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9,
                R.id.btnDot, R.id.btnDiv, R.id.btnMul, R.id.btnMinus, R.id.btnPlus,
                R.id.btnClear, R.id.btnDelete, R.id.btnEqual
        };
        for (int id : keyIds) {
            findViewById(id).setOnClickListener(this);
        }

        updateDisplay();
        resetTimeoutUi();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.btn0) appendDigit("0");
        else if (id == R.id.btn1) appendDigit("1");
        else if (id == R.id.btn2) appendDigit("2");
        else if (id == R.id.btn3) appendDigit("3");
        else if (id == R.id.btn4) appendDigit("4");
        else if (id == R.id.btn5) appendDigit("5");
        else if (id == R.id.btn6) appendDigit("6");
        else if (id == R.id.btn7) appendDigit("7");
        else if (id == R.id.btn8) appendDigit("8");
        else if (id == R.id.btn9) appendDigit("9");
        else if (id == R.id.btnDot) appendDot();
        else if (id == R.id.btnDiv) appendOperator('/');
        else if (id == R.id.btnMul) appendOperator('*');
        else if (id == R.id.btnMinus) appendOperator('-');
        else if (id == R.id.btnPlus) appendOperator('+');
        else if (id == R.id.btnClear) resetCalculator();
        else if (id == R.id.btnDelete) deleteLastChar();
        else if (id == R.id.btnEqual) executeCalculation();
    }


    private void appendDigit(String digit) {
        if (justCalculated) {
            expression.setLength(0);
            txtResult.setText("");
            justCalculated = false;
        }
        expression.append(digit);
        startTimeoutIfNeeded();
        updateDisplay();
    }

    private void appendDot() {
        if (justCalculated) {
            expression.setLength(0);
            txtResult.setText("");
            justCalculated = false;
        }
        int i = expression.length() - 1;
        while (i >= 0 && (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.')) {
            if (expression.charAt(i) == '.') return;
            i--;
        }
        if (expression.length() == 0 || isOperator(expression.charAt(expression.length() - 1))) {
            expression.append("0");
        }
        expression.append(".");
        startTimeoutIfNeeded();
        updateDisplay();
    }

    private void appendOperator(char op) {
        if (justCalculated) {
            String raw = lastRawResult;
            expression.setLength(0);
            expression.append(raw);
            txtResult.setText("");
            justCalculated = false;
        }
        if (expression.length() > 0) {
            char last = expression.charAt(expression.length() - 1);
            if (isOperator(last)) {
                expression.setCharAt(expression.length() - 1, op);
                updateDisplay();
                return;
            }
            if (last == '.') expression.append('0');
        }
        expression.append(op);
        startTimeoutIfNeeded();
        updateDisplay();
    }

    private void deleteLastChar() {
        if (justCalculated) {
            txtResult.setText("");
            justCalculated = false;
        }
        if (expression.length() > 0) {
            expression.deleteCharAt(expression.length() - 1);
        }
        if (expression.length() == 0) {
            stopTimeout();
            resetTimeoutUi();
        }
        updateDisplay();
    }

    private void resetCalculator() {
        expression.setLength(0);
        justCalculated = false;
        lastRawResult = "";
        stopTimeout();
        resetTimeoutUi();
        txtResult.setText("");
        updateDisplay();
    }

    private String lastRawResult = "";

    private void executeCalculation() {
        if (expression.length() == 0 || justCalculated) return;

        String expr = expression.toString();


        stopTimeout();

        try {
            double value = new Parser(expr).parse();
            if (Double.isNaN(value) || Double.isInfinite(value)) {
                throw new ArithmeticException("invalid");
            }
            DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("vi", "VN"));
            DecimalFormat df = new DecimalFormat("#,##0.##########", symbols);
            txtResult.setText("=" + df.format(value));
            lastRawResult = BigDecimal.valueOf(value).stripTrailingZeros().toPlainString();
            justCalculated = true;
        } catch (ArithmeticException | NumberFormatException e) {
            txtResult.setText("Lỗi");
            lastRawResult = "";
            justCalculated = true;
        }
        updateDisplay();
    }

    private static class Parser {
        private final String s;
        private int pos = 0;

        Parser(String s) { this.s = s; }

        double parse() {
            double v = parseExpression();
            if (pos != s.length()) throw new NumberFormatException("unexpected: " + s.charAt(pos));
            return v;
        }

        private double parseExpression() {
            double v = parseTerm();
            while (pos < s.length() && (s.charAt(pos) == '+' || s.charAt(pos) == '-')) {
                char op = s.charAt(pos++);
                double r = parseTerm();
                v = (op == '+') ? v + r : v - r;
            }
            return v;
        }

        private double parseTerm() {
            double v = parseFactor();
            while (pos < s.length() && (s.charAt(pos) == '*' || s.charAt(pos) == '/')) {
                char op = s.charAt(pos++);
                double r = parseFactor();
                if (op == '*') {
                    v *= r;
                } else {
                    if (r == 0) throw new ArithmeticException("div by zero");
                    v /= r;
                }
            }
            return v;
        }

        private double parseFactor() {
            if (pos < s.length() && s.charAt(pos) == '-') {
                pos++;
                return -parseFactor();
            }
            if (pos < s.length() && s.charAt(pos) == '+') {
                pos++;
                return parseFactor();
            }
            int start = pos;
            while (pos < s.length() && (Character.isDigit(s.charAt(pos)) || s.charAt(pos) == '.')) pos++;
            if (start == pos) throw new NumberFormatException("number expected");
            return Double.parseDouble(s.substring(start, pos));
        }
    }

    private boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }

    private void updateDisplay() {
        SpannableString styled = new SpannableString(expression.toString());
        int blue = ContextCompat.getColor(this, R.color.accent);
        for (int i = 0; i < styled.length(); i++) {
            if (isOperator(styled.charAt(i))) {
                styled.setSpan(new ForegroundColorSpan(blue), i, i + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        txtExpression.setText(styled);
    }

    private void startTimeoutIfNeeded() {
        if (timeoutAnimator != null && timeoutAnimator.isRunning()) return;

        animatorCancelled = false;
        timeoutAnimator = ValueAnimator.ofInt(0, PROGRESS_MAX);
        timeoutAnimator.setDuration(TIMEOUT_MS);
        timeoutAnimator.setInterpolator(new LinearInterpolator());
        timeoutAnimator.addUpdateListener(animation -> {
            int progress = (int) animation.getAnimatedValue();
            pbTimeout.setProgress(progress);
            txtPercent.setText((progress / 10) + "%");
        });
        timeoutAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                animatorCancelled = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (animatorCancelled) return;
                onTimeoutExpired();
            }
        });
        timeoutAnimator.start();
    }

    private void stopTimeout() {
        if (timeoutAnimator != null) {
            animatorCancelled = true;
            timeoutAnimator.cancel();
            timeoutAnimator = null;
        }
    }

    private void onTimeoutExpired() {
        timeoutAnimator = null;
        expression.setLength(0);
        justCalculated = false;
        lastRawResult = "";
        txtResult.setText("");
        updateDisplay();
        resetTimeoutUi();
    }

    private void resetTimeoutUi() {
        pbTimeout.setProgress(0);
        txtPercent.setText("0%");
    }

    @Override
    protected void onDestroy() {
        stopTimeout();
        super.onDestroy();
    }
}