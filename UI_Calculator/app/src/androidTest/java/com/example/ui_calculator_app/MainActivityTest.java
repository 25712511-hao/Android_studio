package com.example.ui_calculator_app;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityScenarioRule<MainActivity> rule =
            new ActivityScenarioRule<>(MainActivity.class);

    private void tap(int... ids) {
        for (int id : ids) onView(withId(id)).perform(click());
    }

    @Test
    public void addition_showsResult() {
        tap(R.id.btn2, R.id.btnPlus, R.id.btn3, R.id.btnEqual);
        onView(withId(R.id.txtResult)).check(matches(withText("=5")));
    }

    @Test
    public void expression_isShownWhileTyping() {
        tap(R.id.btn2, R.id.btnPlus, R.id.btn3);
        onView(withId(R.id.txtExpression)).check(matches(withText("2+3")));
    }

    @Test
    public void divisionByZero_showsSpecificError() {
        tap(R.id.btn5, R.id.btnDiv, R.id.btn0, R.id.btnEqual);
        onView(withId(R.id.txtResult)).check(matches(withText("Không thể chia cho 0")));
    }

    @Test
    public void malformedExpression_showsSpecificError() {
        tap(R.id.btn5, R.id.btnPlus, R.id.btnEqual);
        onView(withId(R.id.txtResult)).check(matches(withText("Biểu thức chưa hợp lệ")));
    }

    @Test
    public void clear_emptiesExpression() {
        tap(R.id.btn7, R.id.btn8, R.id.btnClear);
        onView(withId(R.id.txtExpression)).check(matches(withText("")));
    }

    @Test
    public void delete_removesLastCharacter() {
        tap(R.id.btn1, R.id.btn2, R.id.btnDelete);
        onView(withId(R.id.txtExpression)).check(matches(withText("1")));
    }

    @Test
    public void angleMode_defaultsToDegrees() {
        onView(withId(R.id.txtAngleMode)).check(matches(withText("Chế độ góc: độ (deg)")));
        onView(withId(R.id.btnDeg)).check(matches(withText("rad")));
    }

    @Test
    public void angleMode_toggleUpdatesLabelAndButton() {
        tap(R.id.btnDeg);
        onView(withId(R.id.txtAngleMode)).check(matches(withText("Chế độ góc: radian (rad)")));
        onView(withId(R.id.btnDeg)).check(matches(withText("deg")));
    }

    @Test
    public void rotation_keepsExpressionAndAngleMode() {
        tap(R.id.btn4, R.id.btnPlus, R.id.btn6, R.id.btnDeg);
        rule.getScenario().recreate();
        onView(withId(R.id.txtExpression)).check(matches(withText("4+6")));
        onView(withId(R.id.txtAngleMode)).check(matches(withText("Chế độ góc: radian (rad)")));
    }

    @Test
    public void timeout_progressMovesAfterTyping() throws InterruptedException {
        tap(R.id.btn1);
        Thread.sleep(1500);
        onView(withId(R.id.txtPercent)).check(matches(not(withText("0%"))));
    }

    @Test
    public void timeout_clearsExpressionAfterTenSeconds() throws InterruptedException {
        tap(R.id.btn1, R.id.btn2);
        Thread.sleep(10_500);
        onView(withId(R.id.txtExpression)).check(matches(withText("")));
    }
}