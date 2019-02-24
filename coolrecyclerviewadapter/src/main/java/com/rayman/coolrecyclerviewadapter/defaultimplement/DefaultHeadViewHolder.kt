package com.rayman.coolrecyclerviewadapter.defaultimplement

import android.animation.Animator
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
    private var height = 0f
    private var offset = 0f
    private var onRefreshingListener: (() -> Unit)? = null
    private var isPrepare = false

    override fun onReset() {
        if (view is RefreshHeadView) {
            view.onReset()
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
        if (view is RefreshHeadView) {
            if (height < view.maxHeight * 2) {
                height = offset
                Log.i("rayman", "height:$height")
            }
//            Log.i("rayman", "this.offset:${this.offset}")
//            Log.i("rayman", "-----offset:$offset")
            if (offset < this.offset) {
                height -= (this.offset - offset) * 10
                Log.i("rayman", "------------height:$height")
            }
            this.offset = offset
            if (height > view.maxHeight) {
                if (!isPrepare) {
                    isPrepare = true
                    onPrepare()
                }
            } else {
                if (isPrepare) {
                    isPrepare = false
                    onUnprepare()
                }
            }
            val lp = view.contentLayout.layoutParams
            lp.height = height.toInt()
            view.contentLayout.layoutParams = lp
        }
    }

    override fun onRelease() {
        if (view is RefreshHeadView) {
            val targetHeight = if (height > view.maxHeight) {
                view.maxHeight
            } else {
                0f
            }
            val anim = ValueAnimator.ofFloat(height, targetHeight)
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
                    if (height > view.maxHeight) {
                        onRefreshingListener?.invoke()
                        view.onRefreshing()
                    }
                    height = 0f
                }

                override fun onAnimationCancel(animation: Animator?) {
                }

                override fun onAnimationStart(animation: Animator?) {
                }
            })
            anim.start()
        }
    }

    /**
     * 已经准备完成，此时松开即可
     */
    override fun onPrepare() {
        if (view is RefreshHeadView) {
            view.onPrepare()
        }
    }

    /**
     * 未准备完成，此时松开只是缩回去
     */
    override fun onUnprepare() {
        if (view is RefreshHeadView) {
            view.onUnprepare()
        }
    }

    override fun onRefreshFinish() {

    }

    fun setRefreshingListener(listener: () -> Unit) {
        onRefreshingListener = listener
    }

}