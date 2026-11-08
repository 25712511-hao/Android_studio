package com.example.grab_app_ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class VerificationCodeActivity : AppCompatActivity() {

    private lateinit var boxes: List<TextView>
    private lateinit var etPin: EditText
    private var navigated = false

    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verificaton_code)

        val boxContainer = findViewById<LinearLayout>(R.id.boxContainer)
        boxes = List(6) { index ->
            TextView(this).apply {
                text = "-"
                gravity = Gravity.CENTER
                textSize = 18f
                setTextColor(0xFF333333.toInt())
                setBackgroundResource(R.drawable.bg_pin_box)
                layoutParams = LinearLayout.LayoutParams(dp(36), dp(36)).apply {
                    if (index > 0) marginStart = dp(8)
                }
            }.also { boxContainer.addView(it) }
        }

        findViewById<View>(R.id.btnForgotPin).setOnClickListener {
            Toast.makeText(this, "Forgot PIN", Toast.LENGTH_SHORT).show()
        }

        etPin = findViewById(R.id.etPin)

        etPin.filters = arrayOf(
            InputFilter.LengthFilter(6),
            InputFilter { source, _, _, _, _, _ ->
                if (source.all { it.isDigit() }) null else ""
            }
        )

        etPin.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, st: Int, c: Int, a: Int) {}
            override fun onTextChanged(s: CharSequence?, st: Int, b: Int, c: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val len = s?.length ?: 0
                boxes.forEachIndexed { i, box -> box.text = if (i < len) "•" else "-" }
                if (len == 6 && !navigated) {
                    navigated = true
                    startActivity(
                        Intent(this@VerificationCodeActivity, VerifyAccountActivity::class.java)
                    )
                }
            }
        })

        boxContainer.setOnClickListener { showKeyboard() }
    }

    override fun onResume() {
        super.onResume()
        navigated = false
        etPin.text.clear()
        showKeyboard()
    }

    private fun showKeyboard() {
        etPin.requestFocus()
        etPin.post {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(etPin, InputMethodManager.SHOW_IMPLICIT)
        }
    }
}