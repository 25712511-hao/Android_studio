package com.example.grab_app_ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        findViewById<View>(R.id.btnLogin).setOnClickListener {
            startActivity(Intent(this, PhoneNumberActivity::class.java))
        }
        findViewById<View>(R.id.btnSignUp).setOnClickListener {
            startActivity(Intent(this, PhoneNumberActivity::class.java))
        }
    }
}