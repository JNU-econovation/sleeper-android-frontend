package com.example.sleeper_frontend

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import com.example.sleeper_frontend.api.INetworkService
import com.example.sleeper_frontend.dto.sleep.ResetSettingTimeResponse
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SettingTimeUpdateReceiver : BroadcastReceiver() {
    private lateinit var sharedPref : SharedPreferences
    override fun onReceive(context: Context, intent: Intent) {
        val sharedPref = context.getSharedPreferences("user_info", Context.MODE_PRIVATE)
        if(intent != null) {
            resetSetTime()
        }
    }

    private fun getNetworkService(): INetworkService {
        val interceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()

        val gson : Gson = GsonBuilder()
            .setLenient()
            .create()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.21.2:8082/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()

        return retrofit.create(INetworkService::class.java)
    }
    private fun resetSetTime() {
        Log.d("hyeon", "tryNetwork작동")

        val userPk : Long = sharedPref!!.getLong("userPk", 1L)
        Log.d("hyeon","변수 초기화")

        val resetSettingTimeResponseCall : Call<ResetSettingTimeResponse> = getNetworkService().resetSettingTime(
            userPk = userPk
        )

        Log.d("hyeon","call객체 초기화")
        resetSettingTimeResponseCall.enqueue(object : Callback<ResetSettingTimeResponse> {
            override fun onResponse(call : Call<ResetSettingTimeResponse>, response: Response<ResetSettingTimeResponse>) {
                Log.d("hyeon", "통신 성공")
                if (response.isSuccessful && response.body() != null) {

                    val result: ResetSettingTimeResponse = response.body()!!
                    val resultCode: String = response.code().toString()

                    Log.d("hyeon", resultCode)
                    val success: String = "200";
                    val badRequest: String = "300"
                    val internalServerError: String = "500"


                    if (resultCode == success) {
                        Log.d("hyeon","통신 정상 작동")
                    }
                }
            }
            override fun onFailure(call: Call<ResetSettingTimeResponse>, t: Throwable) {
                Log.d("hyeon", "통신 실패")
                val string = t.message.toString()
                Log.d("hyeon", string)
                Log.d("hyeon", resetSettingTimeResponseCall.toString())
            }
        })
    }
}