package com.rayman.coolrecyclerviewadapter

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import com.rayman.coolrecyclerviewadapter.defaultimplement.DefaultHeadViewHolder
import com.rayman.coolrecyclerviewadapter.defaultimplement.DefaultLoadingViewHolder
import com.rayman.coolrecyclerviewadapter.defaultimplement.DefaultViewHolder
import com.rayman.coolrecyclerviewadapter.view.RefreshHeadView

/**
 * @author 吕少锐 (lvshaorui@parkingwang.com)
 * @version 2018/12/20
 */
abstract class CoolRecyclerViewAdapter<T>(val context: Context, private val layoutResource: Int) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var layoutManager: RecyclerView.LayoutManager? = null
    private var loadState = LOADING_FINISH
    private var isLoadMore = false
    private var recyclerView: RecyclerView? = null
    private val data = arrayListOf<T>()
    private var loadingViewHolder: RecyclerView.ViewHolder? = null
    private var headViewHolder: DefaultHeadViewHolder? = null
    private var refreshHeadView: RefreshHeadView? = null
    private var refreshListener: (() -> Unit)? = null
    private var functionalHolderCount = 1
    private var isTop = false

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

    fun setHeadViewHolder(viewHolder: DefaultHeadViewHolder) {
        headViewHolder = viewHolder
    }

    abstract fun onBindData(data: T, holder: DefaultViewHolder)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_LOADING && isLoadMore) {
            loadingViewHolder?.let { return it }
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_loading_more, parent, false)
            DefaultLoadingViewHolder(view).apply { loadingViewHolder = this }
        } else if (viewType == TYPE_HEAD && isLoadMore) {
            headViewHolder?.let { return it }
            DefaultHeadViewHolder(RefreshHeadView(context)).apply {
                refreshListener?.let { setRefreshingListener(it) }
                headViewHolder = this
            }
        } else {
            onCreateViewHolder(parent)
        }
    }

    fun getData(): ArrayList<T> = data

    override fun getItemCount(): Int = data.size + functionalHolderCount

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
        } else if (holder is IHeadRefreshHolder) {

        } else {
            if (position < data.size + 1) {
                Log.i("rayman", "position:$position")
                onBindData(data[position], holder as DefaultViewHolder)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when {
//            position == 0 -> TYPE_HEAD
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

    @SuppressLint("ClickableViewAccessibility")
    fun addLoadMoreListener(loadMore: () -> Unit) {
        functionalHolderCount = 2
        isLoadMore = true
        recyclerView?.addOnScrollListener(object : LoadMoreScrollListener() {
            override fun loadMore() {
                loadMore()
            }
        })
    }

    fun setOnRefreshingListener(listener: () -> Unit) {
        refreshListener = listener
        headViewHolder?.setRefreshingListener(listener)
    }

    fun refreshFinish() {
        headViewHolder?.onReset()
    }

    private fun isOnTop(): Boolean {
        return (recyclerView?.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition() == 0
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

    companion object {
        private const val TYPE_HEAD = 0
        private const val TYPE_ITEM = 1
        private const val TYPE_LOADING = 2
        const val LOADING_START = 1
        const val LOADING_FINISH = 2
        const val LOADING_END = 3
    }

}