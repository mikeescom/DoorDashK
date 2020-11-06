package com.mikeescom.doordashk.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.mikeescom.doordashk.data.Repository
import com.mikeescom.doordashk.data.models.Restaurant
import com.mikeescom.doordashk.data.models.RestaurantDetail

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private var context: Context? = application.applicationContext
    private var repository: Repository? = null
    private var restaurantsResponseLiveData: LiveData<Array<Restaurant>>? = null
    private var restaurantDetailResponseLiveData: LiveData<RestaurantDetail>? = null

    fun init() {
        repository = Repository(context)
    }

    fun getRestaurantsResponseLiveData(lat: Double, lng: Double): LiveData<Array<Restaurant>>? {
        restaurantsResponseLiveData = repository?.getRestaurantsResponseLiveData(lat, lng)
        return restaurantsResponseLiveData
    }

    fun getRestaurantDetailResponseLiveData(id: Long): LiveData<RestaurantDetail>? {
        restaurantDetailResponseLiveData = repository?.getRestaurantDetailResponseLiveData(id)
        return restaurantDetailResponseLiveData
    }
}