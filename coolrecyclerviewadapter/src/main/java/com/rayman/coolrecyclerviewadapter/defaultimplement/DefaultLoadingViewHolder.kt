package com.rayman.coolrecyclerviewadapter.defaultimplement

import android.graphics.drawable.AnimationDrawable
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.rayman.coolrecyclerviewadapter.ILoadingViewHolder
import com.rayman.coolrecyclerviewadapter.R

/**
 * 默认的加载提示
 * @author 吕少锐 (lvshaorui@parkingwang.com)
 * @version 2019/1/20
 */
class DefaultLoadingViewHolder(view: View) : RecyclerView.ViewHolder(view), ILoadingViewHolder {
    private val ivLoading: ImageView by lazy { view.findViewById<ImageView>(R.id.iv_wait_progress) }
    private val infoLoading: LinearLayout by lazy { view.findViewById<LinearLayout>(R.id.info_loading) }
    private val infoNoMore: TextView by lazy { view.findViewById<TextView>(R.id.info_no_more) }

    override fun onLoadStart() {
        infoLoading.visibility = View.VISIBLE
        infoNoMore.visibility = View.GONE
        (ivLoading.background as AnimationDrawable).start()
    }

    override fun onLoadEnd() {
        infoLoading.visibility = View.GONE
        infoNoMore.visibility = View.VISIBLE
    }

    override fun onLoadFinish() {
        infoLoading.visibility = View.GONE
        infoNoMore.visibility = View.GONE
    }
}