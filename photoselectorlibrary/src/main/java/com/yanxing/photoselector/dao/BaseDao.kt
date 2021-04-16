package com.yanxing.photoselector.dao

import androidx.room.*

@Dao
interface BaseDao<T> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(element: T)

    @Update
    fun update(element: T)
}