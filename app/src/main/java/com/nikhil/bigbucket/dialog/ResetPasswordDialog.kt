package com.nikhil.bigbucket.dialog

import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.nikhil.bigbucket.R

fun Fragment.setupBottomSheetDialog(
    onSendClick: (String) -> Unit
) {
    val dialog = BottomSheetDialog(requireContext(), R.style.DialogStyle)
    val view = layoutInflater.inflate(R.layout.reset_password_dialog, null)
    dialog.setContentView(view)
    dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
    dialog.show()

    val edEmail = view.findViewById<EditText>(R.id.editTextResetPassword)
    val sendButton = view.findViewById<Button>(R.id.sendButton)
    val cancelButton = view.findViewById<Button>(R.id.cancelButton)

    sendButton.setOnClickListener {
        val email = edEmail.text.toString().trim()
        if (email == "") {
            edEmail.requestFocus()
            edEmail.error = "Email can't be empty"
        } else {
            onSendClick(email)
            dialog.dismiss()
        }
    }

    cancelButton.setOnClickListener {
        dialog.dismiss()
    }
}