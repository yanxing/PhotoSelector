package com.yanxing.photoselector.dao

import androidx.room.Database
import androidx.room.RoomDatabase
import com.yanxing.photoselector.model.VideoCache

@Database(entities = [VideoCache::class],version = 1)
abstract class AbstractRoomBase :RoomDatabase(){

    abstract fun getVideoCacheDao():VideoCacheDao

}