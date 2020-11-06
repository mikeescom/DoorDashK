package com.mikeescom.doordashk.data

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import com.mikeescom.doordashk.data.db.AppDatabase
import com.mikeescom.doordashk.data.models.Restaurant
import com.mikeescom.doordashk.data.models.RestaurantDetail
import com.mikeescom.doordashk.data.network.Service
import com.mikeescom.doordashk.utils.SharedPref
import io.reactivex.MaybeObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class Repository(context: Context?) {
    private val db: AppDatabase
    private val service: Service
    private var restaurantsResponseMutableLiveData = MutableLiveData<Array<Restaurant>>()
    private var restaurantDetailResponseMutableLiveData = MutableLiveData<RestaurantDetail>()

    companion object {
        private val TAG = Repository::class.java.simpleName
        private const val BASE_URL = "https://api.doordash.com/"
        private const val ONE_DAY_MILLS = 1000 * 60 * 60 * 24.toLong()
    }

    init {
        restaurantDetailResponseMutableLiveData = MutableLiveData<RestaurantDetail>()
        db = Room.databaseBuilder(
            context!!,
            AppDatabase::class.java, "database-doordash"
        ).build()
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder().addInterceptor(interceptor).build()
        service = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(Service::class.java)
    }

    private fun callRestaurants(lat: Double, lng: Double) {
        Log.i(TAG, "Restaurants - call to server")
        service.getRestaurants(lat, lng)
            .enqueue(object : Callback<Array<Restaurant>>{
            override fun onResponse(call: Call<Array<Restaurant>>, response: Response<Array<Restaurant>>) {
                if (response.body() != null) {
                    insertRestaurants(response.body()!!)
                    restaurantsResponseMutableLiveData.postValue(response.body())
                }
            }
            override fun onFailure(call: Call<Array<Restaurant>>, t: Throwable) {
                restaurantsResponseMutableLiveData.postValue(null)
            }
        })
    }

    fun getRestaurantsResponseLiveData(lat: Double, lng: Double): LiveData<Array<Restaurant>> {
        db.restaurantDao()?.all?.subscribeOn(Schedulers.computation())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe(object : MaybeObserver<Array<Restaurant>> {
                override fun onSubscribe(d: Disposable) {
                    Log.i(TAG, "Restaurants - onSubscribe")
                }

                override fun onSuccess(restaurants: Array<Restaurant>) {
                    Log.i(TAG, "Restaurants - onNext")
                    if (restaurants.isEmpty() || areRestaurantsNeedUpdate()) {
                        callRestaurants(lat, lng)
                    } else {
                        restaurantsResponseMutableLiveData.postValue(restaurants)
                    }
                }

                override fun onError(e: Throwable) {
                    Log.i(TAG, "Restaurants - onError: " + e.message)
                }

                override fun onComplete() {
                    Log.i(TAG, "Restaurants - onComplete")
                }
            })
        return restaurantsResponseMutableLiveData
    }

    private fun callRestaurantDetail(id: Long) {
        Log.i(TAG, "RestaurantDetail - call to server")
        service.getRestaurantDetail(id)
            .enqueue(object : Callback<RestaurantDetail?> {
                override fun onResponse(
                    call: Call<RestaurantDetail?>,
                    response: Response<RestaurantDetail?>
                ) {
                    if (response.body() != null) {
                        insertRestaurantDetail(response.body()!!)
                        restaurantDetailResponseMutableLiveData.postValue(response.body())
                    }
                }

                override fun onFailure(call: Call<RestaurantDetail?>, t: Throwable) {
                    restaurantDetailResponseMutableLiveData.postValue(null)
                }
            })
    }

    fun getRestaurantDetailResponseLiveData(id: Long): LiveData<RestaurantDetail> {
        db.restaurantDetailDao()?.getDetail(id)?.subscribeOn(Schedulers.computation())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe(object : MaybeObserver<RestaurantDetail> {
                override fun onSubscribe(d: Disposable) {
                    Log.i(TAG, "RestaurantDetail - onSubscribe")
                }

                override fun onSuccess(restaurantDetail: RestaurantDetail) {
                    Log.i(TAG, "RestaurantDetail - onNext")
                    restaurantDetailResponseMutableLiveData.postValue(restaurantDetail)
                }

                override fun onError(e: Throwable) {
                    Log.i(TAG, "RestaurantDetail - onError: " + e.message)
                }

                override fun onComplete() {
                    Log.i(TAG, "RestaurantDetail - onComplete")
                    callRestaurantDetail(id)
                }
            })
        return restaurantDetailResponseMutableLiveData
    }

    fun insertRestaurants(restaurants: Array<Restaurant>) {
        db.restaurantDao()?.deleteAll()?.subscribeOn(Schedulers.computation())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe(object : DisposableCompletableObserver() {
                override fun onComplete() {
                    Log.i(TAG, "deleteAll - onComplete")
                }

                override fun onError(e: Throwable) {
                    Log.i(TAG, "deleteAll - onError: " + e.message)
                }
            })
        db.restaurantDao()?.insertAll(restaurants)?.subscribeOn(Schedulers.computation())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe(object : DisposableCompletableObserver() {
                override fun onComplete() {
                    Log.i(TAG, "insertAll - onComplete")
                    SharedPref.write(SharedPref.DB_UPDATE_TIME, Calendar.getInstance().timeInMillis)
                }

                override fun onError(e: Throwable) {
                    Log.i(TAG, "insertAll - onError: " + e.message)
                }
            })
    }

    fun insertRestaurantDetail(detail: RestaurantDetail) {
        db.restaurantDetailDao()?.delete(detail)?.subscribeOn(Schedulers.computation())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe(object : DisposableCompletableObserver() {
                override fun onComplete() {
                    Log.i(TAG, "delete - onComplete")
                }

                override fun onError(e: Throwable) {
                    Log.i(TAG, "delete - onError: " + e.message)
                }
            })
        db.restaurantDetailDao()?.insert(detail)?.subscribeOn(Schedulers.computation())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe(object : DisposableCompletableObserver() {
                override fun onComplete() {
                    Log.i(TAG, "insert - onComplete")
                }

                override fun onError(e: Throwable) {
                    Log.i(TAG, "insert - onError: " + e.message)
                }
            })
    }

    private fun areRestaurantsNeedUpdate(): Boolean {
        val savedTime: Long = SharedPref.read(SharedPref.DB_UPDATE_TIME, 0)
        val currentTime = Calendar.getInstance().timeInMillis
        if (currentTime - savedTime > ONE_DAY_MILLS) {
            Log.i(TAG, "Need to update DB. Updated " + (currentTime - savedTime) / 1000 + " seconds ago")
            return true
        }
        Log.i(TAG, "No need to update DB. Updated " + (currentTime - savedTime) / 1000 + " seconds ago")
        return false
    }
}