package com.rayman.coolrecyclerviewadapter

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View

/**
 * @author 吕少锐 (lvshaorui@parkingwang.com)
 * @version 2019/1/21
 */
class RefreshTouchListener : View.OnTouchListener {
    private var mLastY: Float = -1f
    private var sumOffSet = 0
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        event ?: return false
        if (mLastY == -1f) {
            mLastY = event.rawY
        }
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mLastY = event.rawY
                sumOffSet = 0
            }
            MotionEvent.ACTION_MOVE -> {

            }
        }
        return true
    }
}