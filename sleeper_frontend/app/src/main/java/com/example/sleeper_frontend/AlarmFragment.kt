package com.example.sleeper_frontend

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.sleeper_frontend.api.INetworkService
import com.example.sleeper_frontend.databinding.FragmentAlarmBinding
import com.example.sleeper_frontend.dto.sleep.GetSettingTimeResponse
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Integer.parseInt
import java.net.CookieManager

class AlarmFragment : Fragment(R.layout.fragment_alarm) {

    private lateinit var binding : FragmentAlarmBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentAlarmBinding.inflate(inflater, container, false)

        getSettingTime()

        binding.btnUpdate.setOnClickListener {
            val alarmInsideFragment = AlarmInsideFragment()
            val transaction : FragmentTransaction = requireFragmentManager().beginTransaction()
            transaction.replace(R.id.fl_container, alarmInsideFragment).addToBackStack("AlarmFragment").commit()
        }

        return binding.root
    }

    private fun getNetworkService(): INetworkService {
        val interceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .cookieJar(JavaNetCookieJar(CookieManager()))
            .build()

        val gson : Gson = GsonBuilder()
            .setLenient()
            .create()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.0.110:8080/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()

        return retrofit.create(INetworkService::class.java)
    }

    private fun getSettingTime() {

        val sharedPref = requireActivity().getSharedPreferences("user_info", Context.MODE_PRIVATE)
        val userPk : Long = sharedPref.getLong("userPk", 1L)
        val accessToken : String = sharedPref.getString("accessToken", "").toString()


        val getSettingTimeResponseCall : Call<GetSettingTimeResponse> = getNetworkService().getSettingTime(
            accessToken = accessToken, userPk = userPk)


        getSettingTimeResponseCall.enqueue(object : Callback<GetSettingTimeResponse> {
            override fun onResponse(call : Call<GetSettingTimeResponse>, response: Response<GetSettingTimeResponse>) {

                Log.d("설정 수면 시간 가져오기", "통신 상태 : 성공")
                if (response.isSuccessful && response.body() != null) {

                    val result: GetSettingTimeResponse = response.body()!!
                    val resultCode: String = response.code().toString()

                    Log.d("설정 수면 시간 가져오기", "결과 코드 : $resultCode")
                    val success: String = "200"
                    val badRequest: String = "300"
                    val internalServerError: String = "500"


                    if (resultCode == success) {

                        Log.d("설정 수면 시간 가져오기", "통신 상태 : 정상 통신")

                        val setSleepTime = result.setSleepTime
                        val setWakeTime = result.setWakeTime
                        //보여줘야해. 화면에 오늘의 취침, 오늘의 기상 시간.
                        val setSleepTimeHour = setSleepTime.substring(0 until 2)
                        val setSleepTimeMin = setSleepTime.substring(3 until 5)
                        val setWakeTimeHour = setWakeTime.substring(0 until 2)
                        val setWakeTimeMin = setWakeTime.substring(3 until 5)

                        showSettingTime(setSleepTimeHour, setSleepTimeMin, setWakeTimeHour, setWakeTimeMin)
                    }
                }
            }
            override fun onFailure(call: Call<GetSettingTimeResponse>, t: Throwable) {
                Log.d("설정 수면 시간 가져오기", "통신 상태 : 실패")
                val string = t.message.toString()
                Log.d("설정 수면 시간 가져오기", "예외 메세지 : $string")
                Log.d("설정 수면 시간 가져오기", "요청 내용 : $getSettingTimeResponseCall")
            }
        })
    }

    fun showSettingTime(setSleepTimeHour : String, setSleepTimeMin : String, setWakeTimeHour : String, setWakeTimeMin : String) {
        binding.sleepMeridiem.text = getMeridiem(setSleepTimeHour)
        binding.wakeMeridiem.text = getMeridiem(setWakeTimeHour)

        val setSleepTimeHour = set24HourModeFalse(setSleepTimeHour)
        binding.sleepTime.text = getString(R.string.alarm_frg_textview_time, setSleepTimeHour, setSleepTimeMin)
        val setWakeTimeHour = set24HourModeFalse(setWakeTimeHour)
        binding.wakeTime.text = getString(R.string.alarm_frg_textview_time, setWakeTimeHour, setWakeTimeMin)
    }


    private fun getMeridiem(settingHour : String?) : String {
        val settingHour = parseInt(settingHour)

        val meridiem : String = if (settingHour > 12) {
            "오후"
        } else {
            "오전"
        }

        return meridiem
    }

    private fun set24HourModeFalse(settingHour: String?): String {
        var settingHour = parseInt(settingHour)

        if (settingHour > 13) {
            settingHour -= 12
        }

        return settingHour.toString()
    }
}