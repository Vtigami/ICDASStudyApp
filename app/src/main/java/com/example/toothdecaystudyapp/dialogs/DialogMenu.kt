package com.example.toothdecaystudyapp.dialogs

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import com.example.toothdecaystudyapp.ConfigurationsActivity
import com.example.toothdecaystudyapp.R
import com.example.toothdecaystudyapp.anim.Animations
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class DialogMenu(private val context: Context) {

    /*
    * 0 = continue
    * 1 = restart
    * 2 = return to Menu
    *
    *
    * */

    @SuppressLint("ClickableViewAccessibility")
    suspend fun showMenuDialog(scope: CoroutineScope): Int =
        suspendCancellableCoroutine { continuation ->

            val view = LayoutInflater.from(context)
                .inflate(R.layout.pause_menu_dialog_layout, null)

            val dialog = AlertDialog.Builder(context)
                .setView(view)
                .setCancelable(true)
                .create()

            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

            val btnContinue = view.findViewById<LinearLayout>(R.id.playButton)
            val btnConfig = view.findViewById<LinearLayout>(R.id.configurationButton)
            val btnRestart = view.findViewById<LinearLayout>(R.id.restartButton)
            val btnExit = view.findViewById<LinearLayout>(R.id.returnButton)
            val textTV = view.findViewById<TextView>(R.id.text)



            val anim = Animations(context)
            anim.scaleButtonAnim(btnConfig)
            anim.scaleButtonAnim(btnRestart)
            anim.scaleButtonAnim(btnExit)
            anim.scaleButtonAnim(btnContinue)

            btnContinue.setOnClickListener {
                if (continuation.isActive) {
                    continuation.resume(0)
                }
                dialog.dismiss()
            }

            btnConfig.setOnClickListener {
                if (continuation.isActive) {
                    val intent = Intent(context, ConfigurationsActivity::class.java)
                    context.startActivity(intent)
                }
            }

            btnRestart.setOnClickListener {
                if (continuation.isActive) {
                    continuation.resume(1)
                }
                dialog.dismiss()
            }

            btnExit.setOnClickListener {
                if (continuation.isActive) {
                    continuation.resume(2)
                }
                dialog.dismiss()
            }

            dialog.setOnDismissListener {
                if (continuation.isActive) {
                    continuation.resume(0)
                }
            }

            continuation.invokeOnCancellation {
                dialog.dismiss()
            }

            dialog.show()
        }




}