package com.mikeescom.doordashk.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.mikeescom.doordashk.data.models.RestaurantDetail
import io.reactivex.Completable
import io.reactivex.Maybe

@Dao
interface RestaurantDetailDao {
    @Query("SELECT * FROM restaurantDetail WHERE id = :id")
    fun getDetail(id: Long): Maybe<RestaurantDetail>

    @Query("SELECT Count(*) FROM restaurantDetail WHERE id = :id")
    fun getCount(id: Int): Maybe<Long>

    @Insert
    fun insert(detail: RestaurantDetail): Completable

    @Delete
    fun delete(detail: RestaurantDetail): Completable
}