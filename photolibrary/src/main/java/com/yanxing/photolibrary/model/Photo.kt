package com.yanxing.photolibrary.model

import android.net.Uri

/**
 * @author 李双祥 on 2020/11/9.
 */
data class Photo(
    //为了兼容AndroidQ，路径为content形式
    var path:Uri,
    //1图片，2视频，
    var type:Int=1,
    //视频时长
    var duration:Int=0,
    /**
     * 修改时间,没有就用创建时间
     */
    var time:Long=0):Comparable<Photo>{

    //按照时间排序
    override fun compareTo(other: Photo): Int {
        return (this.time-other.time).toInt()
    }

}