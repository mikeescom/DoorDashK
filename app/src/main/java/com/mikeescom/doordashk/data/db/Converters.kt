package com.mikeescom.doordashk.data.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mikeescom.doordashk.data.models.Address
import com.mikeescom.doordashk.data.models.RestaurantDetail

open class Converters {
    @TypeConverter
    fun fromString(value: String?): Address {
        val listType = object : TypeToken<Address?>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromAddress(address: Address?): String {
        val gson = Gson()
        return gson.toJson(address)
    }
}