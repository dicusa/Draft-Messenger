package com.example.chitchatkt.customtouchlistners

import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs

open class OnSwipeTouchListner: View.OnTouchListener {
    private val gestureDetector = GestureDetector(GestureListener())

    fun onTouch(event: MotionEvent): Boolean{
        return gestureDetector.onTouchEvent(event)
    }


    private inner class GestureListener: GestureDetector.SimpleOnGestureListener(){
        private val Swipe_Threshold = 500
        private val Swipe_Velocity_Threshold = 500

        override fun onDown(e: MotionEvent?): Boolean {
            return true
        }

        override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
            onTouch(e!!)
            return true
        }

        override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
            val result = false
            try {
                val diffY = e2?.y?.minus(e1?.y!!)
                val diffX = e2?.x?.minus(e1?.x!!)

                if(abs(diffX!!) > abs(diffY!!)){
                        if(abs(diffX) > Swipe_Threshold && abs(velocityX) > Swipe_Velocity_Threshold){
                            if(diffX > 0){
                                onSwipeRight()
                            }
                            else{
                                onSwipeLeft()
                            }
                        }
                    }
                else{
//                    onTouch(e)
                }
            }catch (e: Exception){
                e.printStackTrace()
            }
            return result
        }
    }
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        return gestureDetector.onTouchEvent(event)
    }

    open fun onSwipeLeft() {}
    open fun onSwipeRight() {}
    open fun onSwipeTop() {}
    open fun onSwipeBottom() {}

}