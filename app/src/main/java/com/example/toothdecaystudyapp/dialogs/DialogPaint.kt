package com.example.toothdecaystudyapp.dialogs

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialog
import com.bumptech.glide.Glide
import com.example.toothdecaystudyapp.ConfigurationsActivity
import com.example.toothdecaystudyapp.R
import com.example.toothdecaystudyapp.anim.Animations
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class DialogPaint(private val context: Context,
    private val image:String,
    private val stage:Int,
    private val answer:Int) {

    /*
    * 0 = continue
    * 1 = restart
    * 2 = return to Menu
    *
    *
    * */

    @SuppressLint("ClickableViewAccessibility")
    suspend fun showPaintDialog(): Int =
        suspendCancellableCoroutine { continuation ->

            val view = LayoutInflater.from(context)
                .inflate(R.layout.paint_frame_dialog_layout, null)

            val dialog = AlertDialog.Builder(context)
                .setView(view)
                .setCancelable(true)
                .create()

            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

            val btnImage = view.findViewById<ImageView>(R.id.image)
            val stageTV = view.findViewById<TextView>(R.id.stageTV)
            val answerTV = view.findViewById<TextView>(R.id.answerTV)

            //btnImage.background = context.getDrawable(image)
            Glide.with(btnImage.context)
                    .load(image)
                    .into(btnImage)  // loads directly into the ImageView

            stageTV.text = getSelectedValueName(stage)
            answerTV.text = getSelectedValueName(answer)

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

    fun getSelectedValueName(value:Int):String{
        if(value == 6)
            return "HÍGIDO"
        return "ESTÁGIO "+(value+1).toString()
    }




}