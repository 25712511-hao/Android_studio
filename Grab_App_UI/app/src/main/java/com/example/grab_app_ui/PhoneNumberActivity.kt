package com.example.grab_app_ui

import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged

class PhoneNumberActivity : AppCompatActivity() {

    private val countryCode = "+84"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_number)
        applyKeyboardInsets()

        val etPhone = findViewById<EditText>(R.id.etPhone)
        val btnNext = findViewById<Button>(R.id.btnNext)

        findViewById<View>(R.id.btnBack).setOnClickListener { finish() }

        // Chỉ cho nhập chữ số
        etPhone.filters = arrayOf(
            InputFilter { source, _, _, _, _, _ ->
                if (source.all { it.isDigit() }) null else ""
            }
        )

        btnNext.isEnabled = false
        etPhone.doAfterTextChanged { btnNext.isEnabled = (it?.length ?: 0) >= 8 }

        btnNext.setOnClickListener {
            val phone = "$countryCode ${etPhone.text.toString().trim()}"
            startActivity(
                Intent(this, InsertCodeActivity::class.java).putExtra("phone", phone)
            )
        }

        etPhone.requestFocus()
    }
}