package com.yanxing.photoselector.util

import android.content.Context
import androidx.room.Room
import com.yanxing.photoselector.dao.AbstractRoomBase

object RoomUtil {

    lateinit var mRoomDataBase: AbstractRoomBase
    private const val DATABASE_NAME="photo_selector"

    /**
     * 初始化数据库
     */
    fun init(context: Context){
        mRoomDataBase = Room.databaseBuilder(context, AbstractRoomBase::class.java, DATABASE_NAME)
            .allowMainThreadQueries()
            .build()
    }

}