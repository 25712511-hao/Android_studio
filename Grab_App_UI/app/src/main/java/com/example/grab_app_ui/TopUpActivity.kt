package com.example.grab_app_ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.Locale

class TopUpActivity : BottomSheetDialogFragment() {

    var amount: Long = 0
    var onConfirm: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.activity_top_up, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val text = "S\$" + String.format(Locale.US, "%.2f", amount.toDouble())
        view.findViewById<TextView>(R.id.tvAmount).text = text
        view.findViewById<TextView>(R.id.tvTotal).text = text

        view.findViewById<View>(R.id.btnClose).setOnClickListener { dismiss() }
        view.findViewById<View>(R.id.btnTopUp).setOnClickListener {
            onConfirm?.invoke()
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        (dialog as? BottomSheetDialog)
            ?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            ?.setBackgroundColor(Color.TRANSPARENT)
        dialog?.window?.setDimAmount(0.5f)
    }
}