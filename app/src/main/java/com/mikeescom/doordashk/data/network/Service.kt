package com.mikeescom.doordashk.data.network

import com.mikeescom.doordashk.data.models.Restaurant
import com.mikeescom.doordashk.data.models.RestaurantDetail
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface Service {
    @GET("/v2/restaurant/")
    fun getRestaurants(
        @Query("lat") lat: Double,
        @Query("lng") lng: Double
    ): Call<Array<Restaurant>>

    @GET("/v2/restaurant/{id}/")
    fun getRestaurantDetail(@Path("id") id: Long): Call<RestaurantDetail>
}