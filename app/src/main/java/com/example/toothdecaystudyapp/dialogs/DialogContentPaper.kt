package com.example.toothdecaystudyapp.dialogs

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.toothdecaystudyapp.R
import com.example.toothdecaystudyapp.anim.Animations
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class DialogContentPaper(private val context: Context) {

    @SuppressLint("ClickableViewAccessibility")
    suspend fun showContentDialog(stage:Int): Boolean =
        suspendCancellableCoroutine { continuation ->

            val view = LayoutInflater.from(context)
                .inflate(R.layout.content_paper_layout, null)

            val dialog = AlertDialog.Builder(context)
                .setView(view)
                .setCancelable(true)
                .create()

            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

            val anim = Animations(context)

            val layout = view.findViewById<LinearLayout>(R.id.layoutToText)
            val title = view.findViewById<TextView>(R.id.title)
            val btClose = view.findViewById<ImageView>(R.id.close_button)

            anim.scaleButtonAnim(btClose)

            val strings = context.resources.getStringArray(stage)

            title.text=strings[0]

            val startIndex = 1

            strings.drop(startIndex).forEach { it ->
                if (it.startsWith("file:///android_asset/content_images")) {
                    addImageView(layout, it)
                } else if(it.startsWith("/numLine")){

                    addNumberedLine(layout,it)
                }else if(it.startsWith("/caption")){

                    addCaptionTextView(layout,it)
                }else if(it.startsWith("/horLine")){

                    addLineView(layout)
                }else {
                    addTextView(layout, it)
                }
            }





            btClose.setOnClickListener(){
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

    fun addTextView(container: LinearLayout, text: String) {
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(
                container.context.dp(12),
                container.context.dp(8),
                container.context.dp(12),
                container.context.dp(8)
            )

        }

        val textView = TextView(container.context).apply {
            this.text = text
            textSize = 18f
            layoutParams = params
            setTextColor(Color.BLACK)
        }

        container.addView(textView)
    }

    fun addCaptionTextView(container: LinearLayout, text: String) {

        val partes = text.trim().split(Regex("\\s+"), limit = 2)

        val caption = partes[1]

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(
                container.context.dp(12),
                container.context.dp(4),
                container.context.dp(12),
                container.context.dp(4)
            )

        }

        val textView = TextView(container.context).apply {
            this.text = caption
            textSize = 14f
            layoutParams = params
            setTextColor(Color.BLACK)
            textAlignment = View.TEXT_ALIGNMENT_CENTER
        }

        container.addView(textView)
    }

    fun addNumberedLine(container: LinearLayout, text: String){
        val partes = text.trim().split(Regex("\\s+"), limit = 3)

        val numero = partes[1]
        val texto = partes[2]

        val inflater = LayoutInflater.from(context)
        val numLine = inflater.inflate(
            R.layout.numbered_line_layout,
            container,
            false
        )

        numLine.findViewById<TextView>(R.id.classTV).text=numero
        numLine.findViewById<TextView>(R.id.textTV).text=texto

        val params = numLine.layoutParams as ViewGroup.MarginLayoutParams

        params.setMargins(
            container.context.dp(6),
            container.context.dp(6),
            container.context.dp(6),
            container.context.dp(6)
        )

        numLine.layoutParams = params

        container.addView(numLine)
    }

    fun addImageView(container: LinearLayout, imagePath: String) {
        val imageView = ImageView(container.context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(
                    container.context.dp(12),
                    container.context.dp(8),
                    container.context.dp(12),
                    container.context.dp(8)
                )
            }

            adjustViewBounds = true
            scaleType = ImageView.ScaleType.FIT_CENTER
        }

        Glide.with(container.context)
            .load(imagePath)
            .into(imageView)

        container.addView(imageView)
    }

    fun addLineView(container: LinearLayout) {
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            container.context.dp(4)
        ).apply {
            setMargins(
                container.context.dp(6),
                container.context.dp(0),
                container.context.dp(4),
                container.context.dp(6)
            )
        }

        val line = View(container.context).apply {
            layoutParams = params
            setBackgroundColor(Color.parseColor("#4FE4AA"))
        }

        container.addView(line)
    }


    fun Context.dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()


}