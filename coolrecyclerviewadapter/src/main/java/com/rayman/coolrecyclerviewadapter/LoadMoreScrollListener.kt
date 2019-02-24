package com.rayman.coolrecyclerviewadapter

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView

/**
 * @author 吕少锐 (lvshaorui@parkingwang.com)
 * @version 2019/2/24
 */
abstract class LoadMoreScrollListener : RecyclerView.OnScrollListener() {
    private var isSlidingUpward = false
    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
        val manager = recyclerView.layoutManager as LinearLayoutManager
        // 当不滑动时
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            //获取最后一个完全显示的itemPosition
            val lastItemPosition = manager.findLastCompletelyVisibleItemPosition()
            val itemCount = manager.itemCount
            // 判断是否滑动到了最后一个item，并且是向上滑动
            if (lastItemPosition == (itemCount - 1) && isSlidingUpward) {
                //加载更多
                val adapter = recyclerView.adapter
                if (adapter is CoolRecyclerViewAdapter<*>) {
                    adapter.setLoadState(CoolRecyclerViewAdapter.LOADING_START)
                    recyclerView.scrollToPosition(adapter.itemCount - 1)
                    loadMore()
                }
            }
        }
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        isSlidingUpward = dy > 0
    }

    abstract fun loadMore()
}
