package com.example.sleeper_frontend

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.sleeper_frontend.api.INetworkService
import com.example.sleeper_frontend.databinding.FragmentCalendarBinding
import com.example.sleeper_frontend.databinding.FragmentCalendarInsideBinding
import com.example.sleeper_frontend.dto.calendar.ShowDateResponse
import com.example.sleeper_frontend.dto.diary.DeleteDiaryResponse
import com.example.sleeper_frontend.dto.diary.UpdateDiaryRequest
import com.example.sleeper_frontend.dto.diary.UpdateDiaryResponse
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
import java.time.LocalTime


class CalendarInsideFragment : Fragment(R.layout.fragment_calendar_inside) {

    private lateinit var binding: FragmentCalendarInsideBinding
    var diaryPk : Long = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding =  FragmentCalendarInsideBinding.inflate(inflater, container, false)

        initDate()
        initCalendar()

        binding.btnUpdateDiary.setOnClickListener {
            updateDiary(diaryPk)
        }

        binding.btnDeleteDiary.setOnClickListener {
            deleteDiary(diaryPk)
        }

        return binding.root
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
            .baseUrl("http://192.168.21.2:8082/")
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

            when (response.code) {
                400 -> {
                    // todo Control Error
                }
                401 -> {
                    val builder = response.request
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

    private fun initDate() {
        var month = requireArguments().getString("month")
        val day = requireArguments().getString("day")
        val year = requireArguments().getString("year")

        when (month) {
            "01" -> month = "January"
            "02" -> month = "February"
            "03" -> month = "March"
            "04" -> month = "April"
            "05" -> month = "May"
            "06" -> month = "June"
            "07" -> month = "July"
            "08" -> month = "August"
            "09" -> month = "September"
            "10" -> month = "October"
            "11" -> month = "November"
            "12" -> month = "December"
        }
        binding.titleCalendarInsideFrg.text = getString(R.string.calendar_inside_frg_date, month, day, year)
    }

    private fun initCalendar() {
        Log.d("hyeon", "tryNetwork작동")

        val month = requireArguments().getString("month")
        val day = requireArguments().getString("day")
        val year = requireArguments().getString("year")
        val date = "$year-$month-$day"

        val sharedPref = activity?.getSharedPreferences("user_info", Context.MODE_PRIVATE)
        val userPk : Long = sharedPref!!.getLong("userPk", 1L)
        val accessToken : String = sharedPref!!.getString("accessToken", " ").toString()

        Log.d("hyeon","변수 초기화")

        val showDateResponseCall : Call<ShowDateResponse> = getNetworkService().getCalendarInside(
            accessToken = accessToken, date = date, userPk = userPk
        )

        Log.d("hyeon","call객체 초기화")
        showDateResponseCall.enqueue(object : Callback<ShowDateResponse> {
            override fun onResponse(call : Call<ShowDateResponse>, response: Response<ShowDateResponse>) {
                Log.d("hyeon", "통신 성공")
                if (response.isSuccessful && response.body() != null) {

                    val result: ShowDateResponse = response.body()!!
                    val resultCode: String = response.code().toString()

                    Log.d("hyeon", resultCode)
                    val success: String = "200";
                    val badRequest: String = "300"
                    val internalServerError: String = "500"


                    if (resultCode == success) {
                        val actualSleepTime = result.actualSleepTime.toString()
                        val actualWakeTime = result.actualWakeTime.toString()
                        val setSleepTime = result.setSleepTime.toString()
                        val setWakeTime = result.setWakeTime.toString()
                        val content = result.content
                        diaryPk = result.diaryPk

                        val textActualSleepTime = actualSleepTime.substring(11 until 16)
                        val textActualWakeTime = actualWakeTime.substring(11 until 16)
                        val textSetSleepTime = setSleepTime.substring(11 until 16)
                        val textSetWakeTime = setWakeTime.substring(11 until 16)


                        binding.diaryCalendarInsideFrg.text = content
                        binding.goalTimeCalendarInsideFrg.text = getString(R.string.calendar_inside_frg_goal_time, textSetSleepTime, textSetWakeTime)
                        binding.actualTimeCalendarInsideFrg.text = getString(R.string.calendar_frg_stats_real_time, textActualSleepTime, textActualWakeTime)
                    }
                }
            }
            override fun onFailure(call: Call<ShowDateResponse>, t: Throwable) {
                Log.d("hyeon", "통신 실패")
                val string = t.message.toString()
                Log.d("hyeon", string)
                Log.d("hyeon", showDateResponseCall.toString())
            }
        })

    }

    private fun updateDiary(diaryPk : Long) {
        Log.d("hyeon", "tryNetwork작동")


        val content = binding.diaryCalendarInsideFrg.text.toString()

        val sharedPref = activity?.getSharedPreferences("user_info", Context.MODE_PRIVATE)
        val userPk : Long = sharedPref!!.getLong("userPk", 1L)
        val accessToken : String = sharedPref!!.getString("accessToken", " ").toString()

        Log.d("hyeon","변수 초기화")

        val updateDiaryResponseCall : Call<UpdateDiaryResponse> = getNetworkService().updateDiary(
            accessToken = accessToken, diaryPk = diaryPk, UpdateDiaryRequest(content = content, userPk = userPk)
        )

        Log.d("hyeon","call객체 초기화")
        updateDiaryResponseCall.enqueue(object : Callback<UpdateDiaryResponse> {
            override fun onResponse(call : Call<UpdateDiaryResponse>, response: Response<UpdateDiaryResponse>) {
                Log.d("hyeon", "통신 성공")
                if (response.isSuccessful && response.body() != null) {

                    val result: UpdateDiaryResponse = response.body()!!
                    val resultCode: String = response.code().toString()

                    Log.d("hyeon", resultCode)
                    val success: String = "200";
                    val badRequest: String = "300"
                    val internalServerError: String = "500"


                    if (resultCode == success) {
                        Toast
                            .makeText(activity, "감사일기 수정이 완료되었습니다.", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
            override fun onFailure(call: Call<UpdateDiaryResponse>, t: Throwable) {
                Log.d("hyeon", "통신 실패")
                val string = t.message.toString()
                Log.d("hyeon", string)
                Log.d("hyeon", updateDiaryResponseCall.toString())
            }
        })
    }

    private fun deleteDiary(diaryPk : Long) {
        Log.d("hyeon", "tryNetwork작동")

        val sharedPref = activity?.getSharedPreferences("user_info", Context.MODE_PRIVATE)
        val userPk : Long = sharedPref!!.getLong("userPk", 1L)
        val accessToken : String = sharedPref!!.getString("accessToken", " ").toString()

        Log.d("hyeon","변수 초기화")

        val deleteDiaryResponseCall : Call<DeleteDiaryResponse> = getNetworkService().deleteDiary(
            accessToken = accessToken, diaryPk = diaryPk, userPk = userPk
        )

        Log.d("hyeon","call객체 초기화")
        deleteDiaryResponseCall.enqueue(object : Callback<DeleteDiaryResponse> {
            override fun onResponse(call : Call<DeleteDiaryResponse>, response: Response<DeleteDiaryResponse>) {
                Log.d("hyeon", "통신 성공")
                if (response.isSuccessful && response.body() != null) {

                    val result: DeleteDiaryResponse = response.body()!!
                    val resultCode: String = response.code().toString()

                    Log.d("hyeon", resultCode)
                    val success: String = "200";
                    val badRequest: String = "300"
                    val internalServerError: String = "500"


                    if (resultCode == success) {

                        binding.diaryCalendarInsideFrg.text = ""
                        Toast
                            .makeText(activity, "감사일기가 성공적으로 삭제되었습니다.", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
            override fun onFailure(call: Call<DeleteDiaryResponse>, t: Throwable) {
                Log.d("hyeon", "통신 실패")
                val string = t.message.toString()
                Log.d("hyeon", string)
                Log.d("hyeon", deleteDiaryResponseCall.toString())
            }
        })
    }

    override fun onStop() {

        activity?.supportFragmentManager?.popBackStack("CalendarFragment",
            FragmentManager.POP_BACK_STACK_INCLUSIVE
        )

        super.onStop()
    }

}