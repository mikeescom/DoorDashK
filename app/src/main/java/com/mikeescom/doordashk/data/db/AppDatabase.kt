package com.mikeescom.doordashk.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mikeescom.doordashk.data.models.Restaurant
import com.mikeescom.doordashk.data.models.RestaurantDetail

@Database(entities = [Restaurant::class, RestaurantDetail::class], version = 1)
@TypeConverters(
    Converters::class
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun restaurantDao(): RestaurantDao?
    abstract fun restaurantDetailDao(): RestaurantDetailDao?
}