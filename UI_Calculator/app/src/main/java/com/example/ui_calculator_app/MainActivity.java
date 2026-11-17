package com.example.ui_calculator_app;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, InputTimeout.Listener {

    private static final int[] DIGIT_IDS = {
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
            R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9
    };
    private static final int[] OTHER_KEY_IDS = {
            R.id.btnDot, R.id.btnDiv, R.id.btnMul, R.id.btnMinus, R.id.btnPlus,
            R.id.btnClear, R.id.btnDelete, R.id.btnEqual,
            R.id.btnE, R.id.btnMu, R.id.btnSin, R.id.btnDeg
    };

    private final CalculatorActivity engine = new CalculatorActivity();
    private InputTimeout timeout;

    private TextView txtExpression;
    private TextView txtResult;
    private TextView txtPercent;
    private ProgressBar pbTimeout;
    private Button btnDeg;

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

        bindViews();
        bindKeys();
        timeout = new InputTimeout(this);
        render();
    }

    private void bindViews() {
        txtExpression = findViewById(R.id.txtExpression);
        txtResult = findViewById(R.id.txtResult);
        txtPercent = findViewById(R.id.txtPercent);
        pbTimeout = findViewById(R.id.pbTimeout);
        btnDeg = findViewById(R.id.btnDeg);
    }

    private void bindKeys() {
        for (int id : DIGIT_IDS) findViewById(id).setOnClickListener(this);
        for (int id : OTHER_KEY_IDS) findViewById(id).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        int digit = digitOf(id);
        if (digit >= 0) {
            engine.pressDigit((char) ('0' + digit));
        } else if (id == R.id.btnDot) engine.pressDot();
        else if (id == R.id.btnDiv) engine.pressOperator('/');
        else if (id == R.id.btnMul) engine.pressOperator('*');
        else if (id == R.id.btnMinus) engine.pressOperator('-');
        else if (id == R.id.btnPlus) engine.pressOperator('+');
        else if (id == R.id.btnE) engine.pressConstant(ExpressionParser.CHAR_E);
        else if (id == R.id.btnMu) engine.pressConstant(ExpressionParser.CHAR_MU);
        else if (id == R.id.btnSin) engine.pressSin();
        else if (id == R.id.btnDeg) engine.toggleAngleMode();
        else if (id == R.id.btnDelete) engine.pressDelete();
        else if (id == R.id.btnClear) engine.clear();
        else if (id == R.id.btnEqual) engine.pressEqual();

        syncTimeout();
        render();
    }

    private static int digitOf(int viewId) {
        for (int i = 0; i < DIGIT_IDS.length; i++) {
            if (DIGIT_IDS[i] == viewId) return i;
        }
        return -1;
    }

    private void syncTimeout() {
        if (engine.isEditingNonEmpty()) {
            timeout.startIfNotRunning();
        } else if (engine.getStatus() != CalculatorActivity.Status.EDITING) {
            timeout.pause();
        } else {
            timeout.stop();
        }
    }

    @Override
    public void onProgress(int progress) {
        pbTimeout.setProgress(progress);
        txtPercent.setText((progress / 10) + "%");
    }

    @Override
    public void onExpired() {
        engine.clear();
        render();
        onProgress(0);
    }

    private void render() {
        renderExpression();
        renderResult();
        btnDeg.setText(engine.isDegreeMode() ? "rad" : "deg");
    }

    private void renderExpression() {
        SpannableString styled = new SpannableString(engine.getExpression());
        int accent = ContextCompat.getColor(this, R.color.accent);
        for (int i = 0; i < styled.length(); i++) {
            if (ExpressionParser.isOperator(styled.charAt(i))) {
                styled.setSpan(new ForegroundColorSpan(accent), i, i + 1,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        txtExpression.setText(styled);
    }

    private void renderResult() {
        if (engine.getStatus() == CalculatorActivity.Status.ERROR) {
            txtResult.setText("Lỗi");
        } else {
            txtResult.setText(engine.getFormattedResult());
        }
    }

    @Override
    protected void onDestroy() {
        timeout.stop();
        super.onDestroy();
    }
}