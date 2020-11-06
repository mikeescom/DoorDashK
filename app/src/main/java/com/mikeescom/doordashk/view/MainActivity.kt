package com.mikeescom.doordashk.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mikeescom.doordashk.R
import com.mikeescom.doordashk.utils.SharedPref

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        SharedPref.init(this)
    }
}