package com.rayman.coolrecyclerviewadapter

/**
 * @author 吕少锐 (lvshaorui@parkingwang.com)
 * @version 2018/12/21
 */
interface ILoadingViewHolder {
    fun onLoadStart()
    fun onLoadEnd()
    fun onLoadFinish()
}