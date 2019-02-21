package com.rayman.coolrecycleviewadapter_sample

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.rayman.coolrecyclerviewadapter.CoolRecyclerViewAdapter
import com.rayman.coolrecyclerviewadapter.defaultimplement.DefaultViewHolder
import com.rayman.coolrecyclerviewadapter.ILoadingViewHolder
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private var isAdd = false

    private val rvMain by lazy { rv_main }
    private val data = arrayListOf(
            ShowData("1", "a"), ShowData("2", "a"), ShowData("3", "a"),
            ShowData("1", "a"), ShowData("2", "a"), ShowData("3", "a"),
            ShowData("4", "a"), ShowData("5", "b"), ShowData("6", "b"),
            ShowData("4", "b"), ShowData("5", "b"), ShowData("6", "b"),
            ShowData("7", "b"), ShowData("8", "b"), ShowData("9", "c"),
            ShowData("10", "c"), ShowData("11", "c"), ShowData("12", "c"),
            ShowData("10", "c"), ShowData("11", "c"), ShowData("12", "c")
    )

    private val nextData = arrayListOf(
            ShowData("1", "d"), ShowData("2", "d"), ShowData("3", "d"),
            ShowData("1", "d"), ShowData("2", "d"), ShowData("3", "d"),
            ShowData("4", "d"), ShowData("5", "e"), ShowData("6", "e"),
            ShowData("4", "e"), ShowData("5", "e"), ShowData("6", "e"),
            ShowData("7", "e"), ShowData("8", "e"), ShowData("9", "f"),
            ShowData("10", "f"), ShowData("11", "f"), ShowData("12", "f"),
            ShowData("10", "f"), ShowData("11", "f"), ShowData("12", "f")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val adapter = object : CoolRecyclerViewAdapter<ShowData>(this@MainActivity, R.layout.item_rv_defalut) {
            override fun onBindData(data: ShowData, holder: DefaultViewHolder) {
                holder.view.findViewById<TextView>(R.id.tv).text = data.string
            }
        }
        adapter.attachRecyclerView(rvMain)
        adapter.setData(data)
        adapter.addLoadMoreListener {
            if (isAdd) {
                adapter.setLoadState(CoolRecyclerViewAdapter.LOADING_END)
                return@addLoadMoreListener
            }
            isAdd = true
            data.addAll(nextData)
            adapter.setNextData(nextData)
        }
        val view = LayoutInflater.from(this).inflate(R.layout.loading_view, rvMain, false)
        adapter.setLoadingViewHolder(MyLoadingViewHolder(view))
    }

    class MyLoadingViewHolder(view: View) : RecyclerView.ViewHolder(view), ILoadingViewHolder {
        private val tv = view.findViewById<TextView>(R.id.tv)

        override fun onLoadStart() {
            tv.text = "Loading"
        }

        override fun onLoadEnd() {
            tv.text = "Load End"
        }

        override fun onLoadFinish() {
            tv.text = "Loading Finish"
        }

    }
}
