package com.mikeescom.doordashk.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class RestaurantDetail (
    @PrimaryKey
    @ColumnInfo(name = "id")
    @SerializedName("id")
    var id: Long,

    @ColumnInfo(name = "name")
    @SerializedName("name")
    var name: String?,

    @ColumnInfo(name = "cover_img_url")
    @SerializedName("cover_img_url")
    var cover_img_url: String?,

    @ColumnInfo(name = "phone_number")
    @SerializedName("phone_number")
    var phone_number : Double,

    @ColumnInfo(name = "description")
    @SerializedName("description")
    var description: String?,

    @ColumnInfo(name = "header_img_url")
    @SerializedName("header_img_url")
    var header_img_url: String?,

    @ColumnInfo(name = "address")
    @SerializedName("address")
    var address: Address?
)

