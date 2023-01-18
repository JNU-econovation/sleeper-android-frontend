package com.example.sleeper_frontend

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentTransaction
import com.example.sleeper_frontend.api.INetworkService
import com.example.sleeper_frontend.databinding.FragmentAlarmBinding
import com.example.sleeper_frontend.dto.sleep.GetGoalTimeRequest
import com.example.sleeper_frontend.dto.sleep.GetGoalTimeResponse
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

class AlarmFragment : Fragment(R.layout.fragment_alarm) {

    private lateinit var binding : FragmentAlarmBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

//        tryNetwork()

        binding = FragmentAlarmBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
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

/*    private fun tryNetwork() {
        Log.d("hyeon", "tryNetwork작동")

        val sharedPref = activity?.getSharedPreferences("user_info", Context.MODE_PRIVATE)
        val userPk : Long = sharedPref?.getLong("userPk", 1L)

        Log.d("hyeon","변수 초기화")

        val initRequest = GetGoalTimeRequest(userPk = userPk)

        val getGoalTimeResponseCall : Call<GetGoalTimeResponse> = getNetworkService().getGoalTime(
            initRequest
        )

        Log.d("hyeon","call객체 초기화")
        getGoalTimeResponseCall.enqueue(object : Callback<GetGoalTimeResponse> {
            override fun onResponse(call : Call<GetGoalTimeResponse>, response: Response<GetGoalTimeResponse>) {
                Log.d("hyeon", "통신 성공")
                if (response.isSuccessful && response.body() != null) {

                    val result: GetGoalTimeResponse? = response.body()
                    val resultCode: String = response.code().toString()

                    Log.d("hyeon", resultCode)
                    val success: String = "200";
                    val badRequest: String = "300"
                    val internalServerError: String = "500"


                    if (resultCode == success) {

                        //보여줘야해. 화면에 오늘의 취침, 오늘의 기상 시간.
                        val goalSleepTime = result?.goalSleepTime
                        val goalWakeTime = result?.goalWakeTime

                        val goalSleepTimeHour = goalSleepTime?.substring(0 until 2)
                        val goalSleepTimeMin = goalSleepTime?.substring(3 until 5)
                        val goalWakeTimeHour = goalWakeTime?.substring(0 until 2)
                        val goalWakeTimeMin = goalWakeTime?.substring(3 until 5)

                        showGoalTime(goalSleepTimeHour, goalSleepTimeMin, goalWakeTimeHour, goalWakeTimeMin)
                    }
                }
            }
            override fun onFailure(call: Call<GetGoalTimeResponse>, t: Throwable) {
                Log.d("hyeon", "통신 실패")
                val string = t.message.toString()
                Log.d("hyeon", string)
                Log.d("hyeon", getGoalTimeResponseCall.toString())
            }
        })
    }

    fun showGoalTime(goalSleepTimeHour : String?, goalSleepTimeMin : String?, goalWakeTimeHour : String?, goalWakeTimeMin : String?) {
        binding.sleepMeridiem.text = getMeridiem(goalSleepTimeHour)
        binding.wakeMeridiem.text = getMeridiem(goalWakeTimeHour)

        val goalSleepTimeHour = set24HourModeFalse(goalSleepTimeHour)
        binding.sleepTime.text = getString(R.string.alarm_frg_textview_time, goalSleepTimeHour, goalSleepTimeMin)
        val goalWakeTimeHour = set24HourModeFalse(goalWakeTimeHour)
        binding.sleepTime.text = getString(R.string.alarm_frg_textview_time, goalWakeTimeHour, goalWakeTimeMin)
    }

    private fun getMeridiem(goalHour : String?) : String {
        val goalHour = parseInt(goalHour)

        val meridiem : String = if (goalHour > 12) {
            "오후"
        } else {
            "오전"
        }

        return meridiem
    }

    private fun set24HourModeFalse(goalHour: String?): String {
        var goalHour = parseInt(goalHour)

        if (goalHour > 13) {
            goalHour -= 12
        }

        return goalHour.toString()
    }*/
}