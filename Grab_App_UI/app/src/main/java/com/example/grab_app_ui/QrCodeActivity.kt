package com.example.grab_app_ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class QrCodeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_code)

        findViewById<View>(R.id.btnBackCamera).setOnClickListener { finish() }

        val openEnterAmount = View.OnClickListener {
            startActivity(Intent(this, EnterAmountActivity::class.java))
        }
        findViewById<View>(R.id.ivQr).setOnClickListener(openEnterAmount)
        findViewById<View>(R.id.ivBarcode).setOnClickListener(openEnterAmount)
    }
}