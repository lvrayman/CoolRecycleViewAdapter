package com.rayman.coolrecyclerviewadapter.defaultimplement

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.rayman.coolrecyclerviewadapter.IHeadRefreshHolder
import com.rayman.coolrecyclerviewadapter.R

/**
 * @author 吕少锐 (lvshaorui@parkingwang.com)
 * @version 2019/1/20
 */
class DefaultHeadViewHolder(view: View) : RecyclerView.ViewHolder(view), IHeadRefreshHolder {
    private val tv = view.findViewById<TextView>(R.id.tv_head).apply {
        val layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0)
        layoutParams.setMargins(0, 0, 0, 0)
        this.layoutParams = layoutParams
        this.setPadding(0, 0, 0, 0)
    }

    override fun onPrepare() {

    }

    override fun onMove(offset: Float) {
        tv.layoutParams.height = offset.toInt()
    }

    override fun onRefresh() {
    }

    override fun onRefreshFinish() {
    }

}