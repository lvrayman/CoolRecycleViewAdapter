package com.rayman.coolrecyclerviewadapter

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.rayman.coolrecyclerviewadapter.defaultimplement.DefaultHeadViewHolder
import com.rayman.coolrecyclerviewadapter.defaultimplement.DefaultLoadingViewHolder
import com.rayman.coolrecyclerviewadapter.defaultimplement.DefaultViewHolder

/**
 * @author 吕少锐 (lvshaorui@parkingwang.com)
 * @version 2018/12/20
 */
abstract class CoolRecyclerViewAdapter<T>(val context: Context, private val layoutResource: Int) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var loadState = LOADING_FINISH
    private var isLoadMore = false
    private var recyclerView: RecyclerView? = null
    private val data = arrayListOf<T>()
    var layoutManager: RecyclerView.LayoutManager? = null
    private var loadingViewHolder: RecyclerView.ViewHolder? = null
    private var headViewHolder: RecyclerView.ViewHolder? = null

    fun onCreateViewHolder(parent: ViewGroup): DefaultViewHolder {
        val view = LayoutInflater.from(context).inflate(layoutResource, parent, false)
        return DefaultViewHolder(view)
    }

    fun setLoadingViewHolder(viewHolder: RecyclerView.ViewHolder) {
        if (viewHolder !is ILoadingViewHolder) {
            throw IllegalArgumentException("The view holder must implement interface ILoadingViewHolder")
        } else {
            loadingViewHolder = viewHolder
        }
    }

    fun setHeadViewHolder(viewHolder: RecyclerView.ViewHolder) {
        if (viewHolder !is IHeadRefreshHolder) {
            throw IllegalArgumentException("The view holder must implement interface IHeadViewHolder")
        } else {
            headViewHolder = viewHolder
        }
    }

    abstract fun onBindData(data: T, holder: DefaultViewHolder)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_LOADING && isLoadMore) {
            loadingViewHolder?.let { return it }
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_loading_more, parent, false)
            DefaultLoadingViewHolder(view)
        } else if (viewType == TYPE_HEAD) {
            headViewHolder?.let { return it }
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_head, parent, false)
            DefaultHeadViewHolder(view)
        } else {
            onCreateViewHolder(parent)
        }
    }

    fun getData(): ArrayList<T> = data

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (isLoadMore && holder is ILoadingViewHolder) {
            when (loadState) {
                LOADING_START -> {
                    holder.onLoadStart()
                }
                LOADING_FINISH -> {
                    holder.onLoadFinish()
                }
                LOADING_END -> {
                    holder.onLoadEnd()
                }
            }
        } else {
            if (position < data.size) {
                onBindData(data[position], holder as DefaultViewHolder)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            position == 0 -> TYPE_HEAD
            position + 1 == itemCount -> TYPE_LOADING
            else -> TYPE_ITEM
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        val manager = recyclerView.layoutManager
        if (manager is GridLayoutManager && isLoadMore) {
            manager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (getItemViewType(position) == TYPE_LOADING) {
                        manager.spanCount
                    } else {
                        1
                    }
                }
            }
        }
    }

    fun setLoadState(loadState: Int) {
        this.loadState = loadState
        notifyDataSetChanged()
    }

    fun setData(newData: List<T>) {
        data.clear()
        data.addAll(newData)
        this.notifyDataSetChanged()
    }

    fun setNextData(nextData: List<T>) {
        if (isLoadMore) {
            if (data.isEmpty()) {
                setLoadState(LOADING_END)
            } else {
                setLoadState(LOADING_FINISH)
                data.addAll(nextData)
                this.notifyDataSetChanged()
            }
        }
    }

    fun attachRecyclerView(recyclerView: RecyclerView) {
        this.recyclerView = recyclerView
        if (layoutManager == null) {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = this
    }

    fun addLoadMoreListener(loadMore: () -> Unit) {
        isLoadMore = true
        recyclerView?.addOnScrollListener(object : LoadMoreScrollListener() {
            override fun loadMore() {
                loadMore()
            }
        })
    }

    /**
     * 添加修饰线，默认是水平方向的分割线
     */
    fun addItemDecoration(
            itemDecoration: RecyclerView.ItemDecoration = DividerItemDecoration(
                    context,
                    DividerItemDecoration.VERTICAL
            )
    ) {
        recyclerView?.addItemDecoration(itemDecoration)
    }

    private abstract inner class LoadMoreScrollListener : RecyclerView.OnScrollListener() {
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
                        adapter.setLoadState(LOADING_START)
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

    companion object {
        private const val TYPE_HEAD = 0
        private const val TYPE_ITEM = 1
        private const val TYPE_LOADING = 2
        const val LOADING_START = 1
        const val LOADING_FINISH = 2
        const val LOADING_END = 3
    }

}