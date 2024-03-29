package com.yanxing.photoselector.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
/**
 * @author 李双祥 on 2020/11/9.
 */
@Parcelize//需要kotlin1.1.4+
data class Photo(
    //路径为content形式
    var path:Uri?=null,
    //1图片，2视频，-1相机（用于点击拍照的）
    var type:Int=1,
    //视频时长，单位为秒
    var duration:Int=0,
    /**
     * 修改时间,没有就用创建时间
     */
    var time:Long=0,
    /**
     * 记录选中
     */
    var select:Boolean=false): Parcelable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Photo
        //只要路径一样，就认为同一个
        if (path != other.path) return false
        return true
    }


}