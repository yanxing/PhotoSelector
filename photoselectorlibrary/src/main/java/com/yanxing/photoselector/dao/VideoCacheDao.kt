package com.yanxing.photoselector.dao

import androidx.room.Dao
import androidx.room.Query
import com.yanxing.photoselector.model.VideoCache

@Dao
interface VideoCacheDao :BaseDao<VideoCache>{

    @Query("select * from video_cache")
    fun findAll():List<VideoCache>
}