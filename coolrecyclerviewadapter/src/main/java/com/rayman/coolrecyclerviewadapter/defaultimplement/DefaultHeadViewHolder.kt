package com.rayman.coolrecyclerviewadapter.defaultimplement

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.TextView
import com.rayman.coolrecyclerviewadapter.IHeadRefreshHolder
import com.rayman.coolrecyclerviewadapter.R
import com.rayman.coolrecyclerviewadapter.view.RefreshHeadView

/**
 * @author 吕少锐 (lvshaorui@parkingwang.com)
 * @version 2019/1/20
 */
class DefaultHeadViewHolder(private val view: View) : RecyclerView.ViewHolder(view), IHeadRefreshHolder {

    private val tv = view.findViewById<TextView>(R.id.tv_head)
    private var offset = 0f
    private var onRefreshingListener: (() -> Unit)? = null

    override fun onReset() {
        if (view is RefreshHeadView) {
            val lp = view.contentLayout.layoutParams
            val anim = ValueAnimator.ofInt(lp.height, 0)
            anim.duration = 250
            anim.addUpdateListener {
                val value = it.animatedValue as Int
                lp.height = value
                view.contentLayout.layoutParams = lp
            }
            anim.start()
        }
    }

    override fun onMove(offset: Float) {
        Log.i("rayman", "offset:$offset")
        if (view is RefreshHeadView) {
            if (offset > view.maxHeight * 1.5) {
                Log.i("rayman", "-----------------return-----------------")
                return
            }
            this.offset = offset
            val lp = view.contentLayout.layoutParams
            lp.height = offset.toInt()
            view.contentLayout.layoutParams = lp
        }
    }

    override fun onRelease() {
        if (view is RefreshHeadView) {
            val targetHeight = if (offset > view.maxHeight) {
                view.maxHeight
            } else {
                0f
            }
            val anim = ValueAnimator.ofFloat(offset, targetHeight)
            anim.duration = 250
            anim.addUpdateListener {
                val value = it.animatedValue as Float
                val lp = view.contentLayout.layoutParams
                lp.height = value.toInt()
                view.contentLayout.layoutParams = lp
            }
            anim.addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) {
                }

                override fun onAnimationEnd(animation: Animator?) {
                    // 动画结束时开始刷新
                    if (offset > view.maxHeight) {
                        onRefreshingListener?.invoke()
                    }
                }

                override fun onAnimationCancel(animation: Animator?) {
                }

                override fun onAnimationStart(animation: Animator?) {
                }
            })
            anim.start()
        }
    }

    override fun onPrepare() {

    }

    override fun onRefreshFinish() {
    }

    fun setRefreshingListener(listener: () -> Unit) {
        onRefreshingListener = listener
    }

}