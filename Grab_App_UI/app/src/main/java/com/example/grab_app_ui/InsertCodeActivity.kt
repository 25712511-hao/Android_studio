package com.example.grab_app_ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.text.InputFilter
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged

class InsertCodeActivity : AppCompatActivity() {

    private lateinit var etCode: EditText
    private lateinit var ivClear: View
    private lateinit var tvResend: TextView

    private var timer: CountDownTimer? = null
    private var navigated = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insert_code)
        applyKeyboardInsets() // <-- thêm dòng này: đẩy phần dưới lên sát bàn phím

        etCode = findViewById(R.id.etCode)
        ivClear = findViewById(R.id.ivClear)
        tvResend = findViewById(R.id.tvResend)

        findViewById<TextView>(R.id.tvPhone).text = intent.getStringExtra("phone").orEmpty()

        findViewById<View>(R.id.btnBack).setOnClickListener { finish() }

        setupCodeInput()
        startResendTimer()
        showKeyboard()
    }

    private fun setupCodeInput() {
        etCode.filters = arrayOf(
            InputFilter.LengthFilter(6),
            InputFilter { source, _, _, _, _, _ ->
                if (source.all { it.isDigit() }) null else ""
            }
        )

        etCode.doAfterTextChanged { text ->
            ivClear.visibility = if (text.isNullOrEmpty()) View.GONE else View.VISIBLE

            if (text?.length == 6 && !navigated) {
                navigated = true
                startActivity(Intent(this, VerificationCodeActivity::class.java))
            }
        }

        ivClear.setOnClickListener { etCode.text.clear() }
    }

    private fun startResendTimer() {
        tvResend.isEnabled = false
        tvResend.setTextColor(ContextCompat.getColor(this, R.color.gray))

        timer?.cancel()
        timer = object : CountDownTimer(60_000, 1_000) {
            override fun onTick(millisUntilFinished: Long) {
                val s = millisUntilFinished / 1000
                tvResend.text = "Request new code in 00:%02d".format(s)
            }

            override fun onFinish() {
                tvResend.text = "Request new code"
                tvResend.setTextColor(Color.parseColor("#58BC6B"))
                tvResend.isEnabled = true
            }
        }.start()

        tvResend.setOnClickListener {
            Toast.makeText(this, "Code resent", Toast.LENGTH_SHORT).show()
            etCode.text.clear()
            startResendTimer()
        }
    }

    private fun showKeyboard() {
        etCode.requestFocus()
        etCode.post {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(etCode, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    override fun onRestart() {
        super.onRestart()
        navigated = false
    }

    override fun onDestroy() {
        timer?.cancel()
        super.onDestroy()
    }
}