package com.mikeescom.doordashk.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class Restaurant (
    @PrimaryKey
    @ColumnInfo(name = "id")
    @SerializedName("id")
    var id: Long,

    @ColumnInfo(name = "delivery_fee")
    @SerializedName("delivery_fee")
    var delivery_fee: Double,

    @ColumnInfo(name = "name")
    @SerializedName("name")
    var name: String?,

    @ColumnInfo(name = "asap_time")
    @SerializedName("asap_time")
    var asap_time: Int,

    @ColumnInfo(name = "average_rating")
    @SerializedName("average_rating")
    var average_rating: Double,

    @ColumnInfo(name = "number_of_ratings")
    @SerializedName("number_of_ratings")
    var number_of_ratings: Long,

    @ColumnInfo(name = "cover_img_url")
    @SerializedName("cover_img_url")
    var cover_img_url: String?,

    @ColumnInfo(name = "header_img_url")
    @SerializedName("header_img_url")
    var header_img_url: String?
)