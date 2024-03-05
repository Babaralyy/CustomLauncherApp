package com.codecoy.balancelauncherapp.swipegesture

import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import com.codecoy.balancelauncherapp.callbacks.SwipeLeftRightCallback
import kotlin.math.abs

class SwipeListener(private val swipeLeftRightCallback: SwipeLeftRightCallback, view: View) : View.OnTouchListener {
    private val tag = "SwipeListener"
    private var gestureDetector: GestureDetector

    init {
        val swipeThreshold = 50
        val velocityThreshold = 50

        val listener: GestureDetector.SimpleOnGestureListener =
            object :
                GestureDetector.SimpleOnGestureListener() {
                override fun onDown(e: MotionEvent): Boolean {
                    return true
                }

                override fun onFling(
                    e1: MotionEvent?,
                    e2: MotionEvent,
                    velocityX: Float,
                    velocityY: Float,
                ): Boolean {
                    val xDiff = e2.x - e1?.x!!
                    val yDiff = e2.y - e1.y

                    try {
                        if (abs(xDiff) > abs(yDiff)) {
                            if (abs(xDiff) > swipeThreshold && abs(velocityX) > velocityThreshold) {
                                if (xDiff > 0) {
                                    swipeLeftRightCallback.onSwipeRight()
                                } else {
                                    swipeLeftRightCallback.onSwipeLeft()
                                }
                                return true
                            }
                        } else {
                            if (abs(yDiff) > swipeThreshold && abs(velocityY) > velocityThreshold) {
                                if (yDiff > 0) {
                                    Log.i(tag, "onFling:: Down")
                                } else {
                                    swipeLeftRightCallback.onSwipeUp()
                                }
                                return true
                            }
                        }
                    } catch (e: Exception) {
                        Log.i(tag, "onFling:: ${e.message}")
                        e.printStackTrace()
                    }
                    return false
                }
            }
        gestureDetector = GestureDetector(listener)
        view.setOnTouchListener(this)
    }

    override fun onTouch(
        p0: View?,
        p1: MotionEvent?,
    ): Boolean {
        return p1?.let { gestureDetector.onTouchEvent(it) } == true
    }
}
