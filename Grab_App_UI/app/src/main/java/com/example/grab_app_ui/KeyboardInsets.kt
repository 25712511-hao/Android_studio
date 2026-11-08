package com.example.grab_app_ui

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding

fun AppCompatActivity.applyKeyboardInsets() {
    WindowCompat.setDecorFitsSystemWindows(window, false)
    val root = findViewById<View>(android.R.id.content)
    ViewCompat.setOnApplyWindowInsetsListener(root) { v, insets ->
        val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
        val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        v.updatePadding(bottom = maxOf(ime.bottom, bars.bottom))
        insets
    }
}