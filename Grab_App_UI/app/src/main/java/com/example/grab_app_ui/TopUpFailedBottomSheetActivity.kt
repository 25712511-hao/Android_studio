package com.example.grab_app_ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class TopUpFailedBottomSheetActivity : BottomSheetDialogFragment() {

    var onTryAgain: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.activity_top_uo_unsucessful, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<View>(R.id.btnTryAgain).setOnClickListener {
            onTryAgain?.invoke()
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