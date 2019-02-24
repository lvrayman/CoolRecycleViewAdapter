package com.rayman.coolrecyclerviewadapter

/**
 * @author 吕少锐 (lvshaorui@parkingwang.com)
 * @version 2019/1/20
 */
interface IHeadRefreshHolder {

    fun onReset()
    fun onPrepare()
    fun onUnprepare()
    fun onMove(offset: Float)
    fun onRelease()
    fun onRefreshFinish()

    companion object {
        //常态
        const val STATE_NORML = 0
        //下拉中
        const val STATE_RELEASE_TO_REFRESH = 1
        //刷新中
        const val STATE_REFRESHING = 2
        //刷新完成
        const val STATE_DONE = 3
    }
}