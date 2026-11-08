package com.example.grab_app_ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment

class CameraPermissionDialog : DialogFragment() {

    companion object {
        const val RESULT_KEY = "camera_permission"
        const val ALLOWED = "allowed"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.dialog_camera_permission, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<View>(R.id.btnDontAllow).setOnClickListener {
            sendResult(false)
            dismiss()
        }

        view.findViewById<View>(R.id.btnOk).setOnClickListener {
            sendResult(true)
            dismiss()
        }
    }

    private fun sendResult(allowed: Boolean) {
        parentFragmentManager.setFragmentResult(
            RESULT_KEY,
            Bundle().apply { putBoolean(ALLOWED, allowed) }
        )
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            setDimAmount(0.5f)
        }
    }
}