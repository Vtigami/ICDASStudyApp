package com.example.toothdecaystudyapp.anim

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.view.MotionEvent
import android.view.View
import android.view.animation.OvershootInterpolator

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


}