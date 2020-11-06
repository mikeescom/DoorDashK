package com.mikeescom.doordashk.data.models

import androidx.room.ColumnInfo
import com.google.gson.annotations.SerializedName

data class Address (
    @ColumnInfo(name = "printable_address")
    @SerializedName("printable_address")
    var printable_address: String?,

    @ColumnInfo(name = "lat")
    @SerializedName("lat")
    var lat : Double,

    @ColumnInfo(name = "lng")
    @SerializedName("lng")
    var lng : Double
)