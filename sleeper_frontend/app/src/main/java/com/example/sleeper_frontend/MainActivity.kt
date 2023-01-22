package com.example.sleeper_frontend

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.sleeper_frontend.api.INetworkService
import com.example.sleeper_frontend.databinding.ActivityMainBinding
import com.example.sleeper_frontend.dto.sleep.GetSettingTimeResponse
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Integer.parseInt
import java.time.ZonedDateTime
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val fragmentHome by lazy { HomeFragment() }
    private val fragmentAlarm by lazy { AlarmFragment() }
    private val fragmentCalendar by lazy { CalendarFragment() }
    private val fragmentMyPage by lazy { MypageFragment() }

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)

        val window = window
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.activityMainLayout.setBackgroundResource(R.drawable.main_background)

        initNavigationBar()
    }

    private fun initNavigationBar() {
        binding.bnvMain.run {
            setOnItemSelectedListener {
                when(it.itemId) {
                    R.id.home -> {
                        changeFragment(fragmentHome)
                    }
                    R.id.alarm -> {
                        changeFragment(fragmentAlarm)
                    }
                    R.id.calendar -> {
                        changeFragment(fragmentCalendar)
                    }
                    R.id.profile -> {
                        changeFragment(fragmentMyPage)
                    }
                }
                true

            }

            binding.bnvMain.selectedItemId = R.id.home
        }

    }

    private fun changeFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fl_container, fragment)
            .commit()
    }

    fun hideBottomNavigation(bool: Boolean) {
        if (bool)
            binding.bnvMain.visibility = View.GONE
        else
            binding.bnvMain.visibility = View.VISIBLE
    }

}