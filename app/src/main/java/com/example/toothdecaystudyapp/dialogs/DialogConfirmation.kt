package com.example.toothdecaystudyapp.dialogs

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import com.example.toothdecaystudyapp.R
import com.example.toothdecaystudyapp.anim.Animations
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class DialogConfirmation(private val context: Context) {

    @SuppressLint("ClickableViewAccessibility")
    suspend fun showConfirmDialog(msg:String=""): Boolean =
        suspendCancellableCoroutine { continuation ->

            val view = LayoutInflater.from(context)
                .inflate(R.layout.confirmation_select_dialog_layout, null)

            val dialog = AlertDialog.Builder(context)
                .setView(view)
                .setCancelable(true)
                .create()

            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

            val btnConfirm = view.findViewById<TextView>(R.id.confirm)
            val btnCancel = view.findViewById<TextView>(R.id.cancel)
            val textTV = view.findViewById<TextView>(R.id.text)

            if(msg.isNotEmpty()){
                textTV.text = msg
            }

            val anim = Animations(context)
            anim.scaleButtonAnim(btnCancel)
            anim.scaleButtonAnim(btnConfirm)

            btnConfirm.setOnClickListener {
                if (continuation.isActive) {
                    continuation.resume(true)
                }
                dialog.dismiss()
            }

            btnCancel.setOnClickListener {
                if (continuation.isActive) {
                    continuation.resume(false)
                }
                dialog.dismiss()
            }

            dialog.setOnDismissListener {
                if (continuation.isActive) {
                    continuation.resume(false)
                }
            }

            continuation.invokeOnCancellation {
                dialog.dismiss()
            }

            dialog.show()
        }




}