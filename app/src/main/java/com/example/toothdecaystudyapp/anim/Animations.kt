package com.example.toothdecaystudyapp.anim

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout
import android.view.Gravity
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener

class Animations(val context: Context) {


    @SuppressLint("ClickableViewAccessibility")
    fun setChangeTouch(button: View, normal: Int, clicked: Int) {
        button.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    button.setBackgroundResource(clicked)
                    button.setPadding(button.paddingLeft, convertToDp(25), button.paddingRight, convertToDp(22))
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    button.setBackgroundResource(normal)

                    button.setPadding(button.paddingLeft, convertToDp(20), button.paddingRight, convertToDp(27))
                }
            }
            false
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun disableChangeTouch(button: View) {
        button.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                }
            }
            false
        }
    }

    fun convertToDp(value: Int): Int {
        val density = Resources.getSystem().displayMetrics.density
        return (value * density).toInt()
    }

    @SuppressLint("ClickableViewAccessibility")
    fun scaleButtonAnim(button: View) {
        button.setOnTouchListener { v, event ->
            when (event.action) {

                MotionEvent.ACTION_DOWN -> {
                    v.animate()
                        .scaleX(0.85f)
                        .scaleY(0.85f)
                        .setDuration(50)
                        .start()
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    v.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(350)
                        .setInterpolator(OvershootInterpolator(4f))
                        .start()
                }
            }

            false
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun scaleLessButtonAnim(button: View) {
        button.setOnTouchListener { v, event ->
            when (event.action) {

                MotionEvent.ACTION_DOWN -> {
                    v.animate()
                        .scaleX(0.95f)
                        .scaleY(0.95f)
                        .setDuration(50)
                        .start()
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    v.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(350)
                        .setInterpolator(OvershootInterpolator(4f))
                        .start()
                }
            }

            false
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    fun liftButtonAnim(view: View) {

        view.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    v.animate()
                        .translationY(convertToDp(50)*(-1f))
                        .setDuration(50)
                        .start()
                }
                MotionEvent.ACTION_UP,
                MotionEvent.ACTION_CANCEL -> {
                    v.animate()
                        .translationY(convertToDp(5)*(-1f))
                        .setDuration(20)
                        .start()
                }
            }
            false
        }
    }




    fun animateNextImage(
        container: FrameLayout,
        imageView: ImageView,
        path: String
    ) {
        // Cria uma cópia independente da imagem atual
        val oldImage = ImageView(imageView.context).apply {

            layoutParams = FrameLayout.LayoutParams(
                imageView.width,
                imageView.height,
                Gravity.CENTER
            )

            setImageDrawable(
                imageView.drawable?.constantState
                    ?.newDrawable()?.mutate()
                    ?: imageView.drawable
            )

            background = imageView.background?.constantState
                ?.newDrawable()?.mutate()
                ?: imageView.background

            scaleType = imageView.scaleType

            setPadding(
                imageView.paddingLeft,
                imageView.paddingTop,
                imageView.paddingRight,
                imageView.paddingBottom
            )

            elevation = imageView.elevation + 10f
        }

        // Adiciona a cópia sobre a imagem original
        container.addView(oldImage)

        // Anima a imagem antiga
        oldImage.animate()
            .translationY(500f)
            .alpha(0f)
            .setDuration(700)
            .setInterpolator(AccelerateInterpolator())
            .withEndAction {
                container.removeView(oldImage)
            }
            .start()

        // Carrega a nova imagem com Glide
        Glide.with(imageView.context)
            .load("file:///android_asset/$path")
            .into(imageView)
    }



}