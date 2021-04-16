package com.yanxing.photoselector.model

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 记录视频路径和时长
 */
@Entity(tableName ="video_cache")
data class VideoCache(
        //视频路径，content形式
        @PrimaryKey
        var path: String,
        //视频时长，单位为秒
        var duration:Int=0
)