package com.example.sleeper_frontend


import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.databinding.adapters.TextViewBindingAdapter.setText
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.sleeper_frontend.api.INetworkService
import com.example.sleeper_frontend.databinding.FragmentAlarmInsideBinding
import com.example.sleeper_frontend.dto.sleep.GetRecommendationResponse
import com.example.sleeper_frontend.dto.sleep.GetSettingTimeResponse
import com.example.sleeper_frontend.dto.sleep.SetAlarmTimeRequest
import com.example.sleeper_frontend.dto.sleep.SetAlarmTimeResponse
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Integer.parseInt
import java.time.LocalTime
import java.time.ZonedDateTime

class AlarmInsideFragment : Fragment(R.layout.fragment_alarm_inside) {

    private lateinit var binding: FragmentAlarmInsideBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentAlarmInsideBinding.inflate(inflater, container, false)

        init()

        binding.btnSetSleepTime.setOnClickListener {
            showTimePicker(it)
        }

        binding.btnSetWakeTime.setOnClickListener {
            showTimePicker(it)
        }

        setOnRecommendBtnListener(binding.rec1, binding.rec1Meridiem, binding.rec1Time)
        setOnRecommendBtnListener(binding.rec2, binding.rec2Meridiem, binding.rec2Time)
        setOnRecommendBtnListener(binding.rec3, binding.rec3Meridiem, binding.rec3Time)
        setOnRecommendBtnListener(binding.rec4, binding.rec4Meridiem, binding.rec4Time)

        binding.btnFinish.setOnClickListener {
            saveAlarmTime()
        }

        return binding.root
    }

    private fun init() {
        getSettingTime()

        getRecommendationTime()
    }

    private fun getSettingTime() {
        Log.d("hyeon", "tryNetwork작동")

        val sharedPref = activity?.getSharedPreferences("user_info", Context.MODE_PRIVATE)
        val userPk : Long = sharedPref!!.getLong("userPk", 1L)
        val accessToken : String = sharedPref!!.getString("accessToken", " ").toString()

        Log.d("hyeon","변수 초기화")

        val getSettingTimeResponseCall : Call<GetSettingTimeResponse> = getNetworkService().getSettingTime(
            accessToken = accessToken, userPk = userPk)

        Log.d("hyeon","call객체 초기화")
        getSettingTimeResponseCall.enqueue(object : Callback<GetSettingTimeResponse> {
            override fun onResponse(call : Call<GetSettingTimeResponse>, response: Response<GetSettingTimeResponse>) {
                Log.d("hyeon", "통신 성공")
                if (response.isSuccessful && response.body() != null) {

                    val result: GetSettingTimeResponse = response.body()!!
                    val resultCode: String = response.code().toString()

                    Log.d("hyeon", resultCode)
                    val success: String = "200";
                    val badRequest: String = "300"
                    val internalServerError: String = "500"


                    if (resultCode == success) {

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
                Log.d("hyeon", "통신 실패")
                val string = t.message.toString()
                Log.d("hyeon", string)
                Log.d("hyeon", getSettingTimeResponseCall.toString())
            }
        })
    }

    fun showSettingTime(setSleepTimeHour : String, setSleepTimeMin : String, setWakeTimeHour : String, setWakeTimeMin : String) {
        binding.textviewAlarmInsideSleepMeridiem.text = getMeridiem(setSleepTimeHour)
        binding.textviewAlarmInsideWakeMeridiem.text = getMeridiem(setWakeTimeHour)

        val setSleepTimeHour = set24HourModeFalse(setSleepTimeHour)
        binding.textviewAlarmInsideSleepTime.text = getString(R.string.alarm_frg_textview_time, setSleepTimeHour, setSleepTimeMin)
        val setWakeTimeHour = set24HourModeFalse(setWakeTimeHour)
        binding.textviewAlarmInsideWakeTime.text = getString(R.string.alarm_frg_textview_time, setWakeTimeHour, setWakeTimeMin)
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

    private fun getRecommendationTime() {
        Log.d("hyeon", "tryNetwork작동")

        val setSleepTime : String = binding.textviewAlarmInsideSleepTime.text.toString()
        val sharedPref = activity?.getSharedPreferences("user_info", Context.MODE_PRIVATE)
        val accessToken : String = sharedPref!!.getString("accessToken", " ").toString()

        Log.d("hyeon","변수 초기화")

        val getRecommendationResponseCall : Call<GetRecommendationResponse> = getNetworkService().getRecommendTime(
            accessToken = accessToken, setSleepTime = setSleepTime)

        Log.d("hyeon","call객체 초기화")
        getRecommendationResponseCall.enqueue(object : Callback<GetRecommendationResponse> {
            override fun onResponse(call : Call<GetRecommendationResponse>, response: Response<GetRecommendationResponse>) {
                Log.d("hyeon", "통신 성공")
                if (response.isSuccessful && response.body() != null) {

                    val result: GetRecommendationResponse = response.body()!!
                    val resultCode: String = response.code().toString()

                    Log.d("hyeon", resultCode)
                    val success: String = "200";
                    val badRequest: String = "300"
                    val internalServerError: String = "500"


                    if (resultCode == success) {
                        val recommendedTimeList : List<String> = result.recommendedTimes
                        val rec1 = recommendedTimeList[0]
                        val rec2 = recommendedTimeList[1]
                        val rec3 = recommendedTimeList[2]
                        val rec4 = recommendedTimeList[3]

                        showRecTime(rec1, binding.rec1Meridiem, binding.rec1Time)
                        showRecTime(rec2, binding.rec2Meridiem, binding.rec2Time)
                        showRecTime(rec3, binding.rec3Meridiem, binding.rec3Time)
                        showRecTime(rec4, binding.rec4Meridiem, binding.rec4Time)

                    }
                }
            }
            override fun onFailure(call: Call<GetRecommendationResponse>, t: Throwable) {
                Log.d("hyeon", "통신 실패")
                val string = t.message.toString()
                Log.d("hyeon", string)
                Log.d("hyeon", getRecommendationResponseCall.toString())
            }
        })
    }

    private fun showRecTime(rectime : String, meridiemView : TextView, timeView : TextView) {
        val rectime : String = rectime
        val hour : String = rectime.substring(0 until 2)
        val min : String = rectime.substring(3 until 5)

        meridiemView.text = getMeridiem(hour)

        val hourText = set24HourModeFalse(hour)
        timeView.text = getString(R.string.alarm_frg_textview_time, hourText, min)
    }

    private fun showTimePicker(btn : View) {

        val timePickerDialog = TimePickerDialogFragment()

        timePickerDialog.setOnClickedListener(object : TimePickerDialogFragment.ButtonClickListener {
            override fun onClicked(hour : Int, minute : Int) {
                setTimeText(btn, hour, minute)
                getRecommendationTime()
            }
        })

        activity?.supportFragmentManager?.let { fragmentManager ->
            timePickerDialog.show(
                fragmentManager,
                "tag"
            )}
    }

    private fun setTimeText(btn : View, hourOfDay : Int, minute : Int) {
        var min : String = minute.toString()

        if ( min.length < 2 ) {
            min = "0${min}"
        }

        val meridiem : String = if (hourOfDay > 12) {
            "오후"
        } else {
            "오전"
        }

        val hour : Int  = if (hourOfDay > 13) {
            hourOfDay - 12
        } else {
            hourOfDay
        }

        if (btn == binding.btnSetSleepTime) {
            binding.textviewAlarmInsideSleepMeridiem.text =meridiem
            binding.textviewAlarmInsideSleepTime.text = getString(R.string.survey_scr_textview, hour, min)
        } else if (btn == binding.btnSetWakeTime) {
            binding.textviewAlarmInsideWakeMeridiem.text = meridiem
            binding.textviewAlarmInsideWakeTime.text = getString(R.string.survey_scr_textview, hour, min)
        }
    }

    private fun getNetworkService(): INetworkService {
        val interceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .addInterceptor(RequestInterceptor())
            .addInterceptor(ResponseInterceptor())
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

    inner class RequestInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
            val sharedPref = activity?.getSharedPreferences("user_info", Context.MODE_PRIVATE)
            val accessToken : String = sharedPref!!.getString("accessToken", " ").toString()

            val builder = chain.request()
                .newBuilder()
                .addHeader("Authorization", accessToken)
                .build()

            return chain.proceed(builder)
        }
    }

    inner class ResponseInterceptor : Interceptor {

        override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
            val request = chain.request()
            val response = chain.proceed(request)

            val sharedPref = activity?.getSharedPreferences("user_info", Context.MODE_PRIVATE)
            val refreshToken : String = sharedPref!!.getString("refreshToken", " ").toString()

            when (response.code()) {
                400 -> {
                    // todo Control Error
                }
                401 -> {
                    val builder = response.request()
                        .newBuilder()
                        .removeHeader("Authorization")
                        .addHeader("Authorization", refreshToken)
                        .build()

                    return chain.proceed(builder)
                }
                402 -> {
                    // todo Control Error
                }
            }
            return response
        }
    }

    private fun setOnRecommendBtnListener(recBtn : AppCompatButton, recMeridiem : TextView, recTime : TextView) {

        recBtn.setOnClickListener {
            binding.textviewAlarmInsideWakeMeridiem.text = recMeridiem.text
            binding.textviewAlarmInsideWakeTime.text = recTime.text

        }
    }

    private fun saveAlarmTime() {
        Log.d("hyeon", "tryNetwork작동")


        val localTime : String = LocalTime.now().toString()
        val zonedDateTimeOffset : String = ZonedDateTime.now().offset.toString()


        val sleepTime : String = binding.textviewAlarmInsideSleepTime.text.toString()
        val zonedDateTimeSleep : String = localTime+"T"+sleepTime+":00"+ zonedDateTimeOffset
        val wakeTime : String = binding.textviewAlarmInsideWakeTime.text.toString()
        var zonedDateTimeWake : String = localTime+"T"+wakeTime+":00"+ zonedDateTimeOffset


        val sharedPref = activity?.getSharedPreferences("user_info", Context.MODE_PRIVATE)
        val userPk : Long = sharedPref!!.getLong("userPk", 1L)
        val accessToken : String = sharedPref.getString("accessToken", " ").toString()

        Log.d("hyeon","변수 초기화")

        val setAlarmTimeResponseCall : Call<SetAlarmTimeResponse> = getNetworkService().setAlarmTime(
            accessToken =accessToken, userPk = userPk, SetAlarmTimeRequest(sleepTime = zonedDateTimeSleep, wakeTime = zonedDateTimeWake, userPk = userPk)
        )

        Log.d("hyeon","call객체 초기화")
        setAlarmTimeResponseCall.enqueue(object : Callback<SetAlarmTimeResponse> {
            override fun onResponse(call : Call<SetAlarmTimeResponse>, response: Response<SetAlarmTimeResponse>) {
                Log.d("hyeon", "통신 성공")
                if (response.isSuccessful && response.body() != null) {

                    val result: SetAlarmTimeResponse = response.body()!!
                    val resultCode: String = response.code().toString()

                    Log.d("hyeon", resultCode)
                    val success: String = "200";
                    val badRequest: String = "300"
                    val internalServerError: String = "500"


                    if (resultCode == success) {
                        activity?.supportFragmentManager?.popBackStack("AlarmFragment",
                            FragmentManager.POP_BACK_STACK_INCLUSIVE
                        )

                        val alarmFragment = AlarmFragment()
                        val transaction : FragmentTransaction = requireFragmentManager().beginTransaction()
                        transaction
                            .replace(R.id.fl_container, alarmFragment)
                            .commit()
                    }
                }
            }
            override fun onFailure(call: Call<SetAlarmTimeResponse>, t: Throwable) {
                Log.d("hyeon", "통신 실패")
                val string = t.message.toString()
                Log.d("hyeon", string)
                Log.d("hyeon", setAlarmTimeResponseCall.toString())
            }
        })
    }
}
