package com.rayman.coolrecyclerviewadapter.defaultimplement

import android.support.v7.widget.RecyclerView
import android.view.View
import com.rayman.coolrecyclerviewadapter.IHeadRefreshHolder

/**
 * @author 吕少锐 (lvshaorui@parkingwang.com)
 * @version 2019/1/20
 */
class DefaultHeadViewHolder(view: View) : RecyclerView.ViewHolder(view),IHeadRefreshHolder {
    override fun onRefresh() {
    }

    override fun onRefreshFinish() {
    }

}