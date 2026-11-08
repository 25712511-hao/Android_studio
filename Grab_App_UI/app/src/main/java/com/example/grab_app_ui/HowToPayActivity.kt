package com.example.grab_app_ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity

class HowToPayActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_how_to_pay)

        setupActions()
        setupBackPress()
    }

    private fun setupActions() {
        findViewById<View>(R.id.btnBack).setOnClickListener {
            finish()
        }

        findViewById<View>(R.id.btnNextQr).setOnClickListener {
            startActivity(Intent(this, QrCodeActivity::class.java))
        }

        findViewById<View>(R.id.btnWhereUse).setOnClickListener {
            Toast.makeText(this, "Where can I use GrabPay?", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupBackPress() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
    }
}