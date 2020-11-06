package com.mikeescom.doordashk.utils

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences

object SharedPref {
    private var mSharedPref: SharedPreferences? = null
    const val DB_UPDATE_TIME = "DB_UPDATE_TIME"
    fun init(context: Context) {
        if (mSharedPref == null) {
            mSharedPref = context.getSharedPreferences(context.packageName, Activity.MODE_PRIVATE)
        }
    }

    fun read(key: String?, defValue: Long): Long {
        return mSharedPref!!.getLong(key, defValue)
    }

    fun write(key: String?, value: Long) {
        val prefsEditor = mSharedPref!!.edit()
        prefsEditor.putLong(key, value)
        prefsEditor.apply()
    }
}
