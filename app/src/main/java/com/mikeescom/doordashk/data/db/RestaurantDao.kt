package com.mikeescom.doordashk.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.mikeescom.doordashk.data.models.Restaurant
import io.reactivex.Completable
import io.reactivex.Maybe

@Dao
interface RestaurantDao {
    @get:Query("SELECT * FROM restaurant")
    val all: Maybe<Array<Restaurant>>

    @get:Query("SELECT Count(*) FROM restaurant")
    val count: Maybe<Long>

    @Insert
    fun insertAll(restaurants: Array<Restaurant>): Completable

    @Query("DELETE FROM restaurant")
    fun deleteAll(): Completable
}