package com.example.grab_app_ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged

class EnterAmountActivity : AppCompatActivity() {

    private var attempts = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_amount)
        applyKeyboardInsets()

        val etAmount = findViewById<EditText>(R.id.etAmount)
        val btnNext = findViewById<Button>(R.id.btnNext)

        findViewById<View>(R.id.btnBack).setOnClickListener { finish() }

        btnNext.isEnabled = false
        etAmount.doAfterTextChanged { text ->
            btnNext.isEnabled = (text?.toString()?.toLongOrNull() ?: 0L) > 0
        }

        mapOf(R.id.q50 to "50", R.id.q100 to "100", R.id.q200 to "200", R.id.q300 to "300")
            .forEach { (id, value) ->
                findViewById<TextView>(id).setOnClickListener {
                    etAmount.setText(value)
                    etAmount.setSelection(value.length)
                }
            }

        btnNext.setOnClickListener {
            val amount = etAmount.text.toString().toLongOrNull() ?: 0L
            showReviewSheet(amount, etAmount)
        }

        etAmount.requestFocus()
    }

    private fun showReviewSheet(amount: Long, etAmount: EditText) {
        val reviewSheet = TopUpActivity()
        reviewSheet.amount = amount
        reviewSheet.onConfirm = {
            attempts++
            if (attempts == 1) {
                showFailedSheet(etAmount)
            } else {
                onTopUpSuccess(amount)
            }
        }
        reviewSheet.show(supportFragmentManager, "review")
    }

    private fun showFailedSheet(etAmount: EditText) {
        val failedSheet = TopUpFailedBottomSheetActivity()
        failedSheet.onTryAgain = { etAmount.requestFocus() }
        failedSheet.show(supportFragmentManager, "topup_failed")
    }

    private fun onTopUpSuccess(amount: Long) {
        Toast.makeText(this, "Top-up successful: S\$$amount", Toast.LENGTH_LONG).show()
        startActivity(
            Intent(this, HomeActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        )
    }
}