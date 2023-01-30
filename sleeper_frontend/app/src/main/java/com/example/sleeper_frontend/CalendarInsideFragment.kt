package com.example.sleeper_frontend

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.sleeper_frontend.api.INetworkService
import com.example.sleeper_frontend.databinding.FragmentCalendarInsideBinding
import com.example.sleeper_frontend.dto.calendar.ShowDateResponse
import com.example.sleeper_frontend.dto.diary.DeleteDiaryResponse
import com.example.sleeper_frontend.dto.diary.UpdateDiaryRequest
import com.example.sleeper_frontend.dto.diary.UpdateDiaryResponse
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
import java.net.CookieManager
import java.time.LocalTime


class CalendarInsideFragment : Fragment(R.layout.fragment_calendar_inside) {

    private lateinit var binding: FragmentCalendarInsideBinding
    var diaryPk : Long = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        binding = FragmentCalendarInsideBinding.inflate(inflater, container, false)

        binding.pieView.setPercentageBackgroundColor(Color.parseColor("#FF8478BD"))
        binding.pieView.setTextColor(Color.parseColor("#ff2C2F6E"))
        binding.pieView.setInnerBackgroundColor(Color.parseColor("#ffF8F8F8"))
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
        Log.d("캘린더 초기화", "통신 상태 : 실행")


        val month = requireArguments().getString("month")
        val day = requireArguments().getString("day")
        val year = requireArguments().getString("year")
        val date = "$year-$month-$day"


        val sharedPref = requireActivity().getSharedPreferences("user_info", Context.MODE_PRIVATE)
        val userPk : Long = sharedPref.getLong("userPk", 1L)
        val accessToken : String = sharedPref.getString("accessToken", " ").toString()

        Log.d("캘린더 초기화", "통신 상태 : 변수 초기화")

        val showDateResponseCall : Call<ShowDateResponse> = getNetworkService().getCalendarInside(
            accessToken = accessToken, date = date, userPk = userPk
        )

        Log.d("캘린더 초기화", "통신 상태 : Call 객체 초기화")

        showDateResponseCall.enqueue(object : Callback<ShowDateResponse> {
            override fun onResponse(call : Call<ShowDateResponse>, response: Response<ShowDateResponse>) {

                Log.d("캘린더 초기화", "통신 상태 : 성공")

                if(response.code() == 404) {

                    Log.d("캘린더 초기화", "통신 상태 : 정상 통신 404")


                    binding.diaryCalendarInsideFrg.setText("작성된 내용이 없습니다.")
                    binding.goalTimeCalendarInsideFrg.text = "-"
                    binding.actualTimeCalendarInsideFrg.text = "-"
                    /*binding.pieView.percentage = 0.toFloat()
                    binding.pieView.setInnerText("")*/
                }

                if (response.isSuccessful && response.body() != null) {

                    val result: ShowDateResponse = response.body()!!
                    val resultCode: String = response.code().toString()

                    Log.d("캘린더 초기화", "결과 코드 : $resultCode")
                    val success: String = "200"
                    val badRequest: String = "300"
                    val notFound : String = "404"
                    val internalServerError: String = "500"


                    if (resultCode == success) {
                        Log.d("캘린더 초기화", "통신 상태 : 정상 통신 200")

                        val actualSleepTime = result.actualSleepTime.toString()
                        Log.d("actualSleepTime", actualSleepTime)
                        val actualWakeTime = result.actualWakeTime.toString()
                        val setSleepTime = result.setSleepTime.toString()
                        val setWakeTime = result.setWakeTime.toString()
                        val content = result.content
                        val score = result.score


                        binding.pieView.setInnerTextVisibility(View.VISIBLE)
                        binding.pieView.percentage = score.toFloat()


                        diaryPk = result.diaryPk

                        val textActualSleepTime = actualSleepTime.substring(12 until 17)
                        val textActualWakeTime = actualWakeTime.substring(12 until 17)
                        val textSetSleepTime = setSleepTime.substring(12 until 17)
                        val textSetWakeTime = setWakeTime.substring(12 until 17)


                        binding.diaryCalendarInsideFrg.setText(content)
                        binding.goalTimeCalendarInsideFrg.text = getString(R.string.calendar_inside_frg_goal_time, textSetSleepTime, textSetWakeTime)
                        binding.actualTimeCalendarInsideFrg.text = getString(R.string.calendar_frg_stats_real_time, textActualSleepTime, textActualWakeTime)
                    }
                }
            }
            override fun onFailure(call: Call<ShowDateResponse>, t: Throwable) {
                Log.d("캘린더 초기화", "통신 상태 : 실패")
                val string = t.message.toString()
                Log.d("캘린더 초기화", "예외 메세지 : $string")
                Log.d("캘린더 초기화", "요청 내용 : $showDateResponseCall")
            }
        })

    }

    private fun updateDiary(diaryPk : Long) {

        val content = binding.diaryCalendarInsideFrg.text.toString()


        val sharedPref = requireActivity().getSharedPreferences("user_info", Context.MODE_PRIVATE)
        val userPk : Long = sharedPref.getLong("userPk", 1L)
        val accessToken : String = sharedPref.getString("accessToken", "").toString()


        Log.d("다이어리 내용 수정", "통신 상태 : 변수 초기화")


        val updateDiaryResponseCall : Call<UpdateDiaryResponse> = getNetworkService().updateDiary(
            accessToken = accessToken, diaryPk = diaryPk, UpdateDiaryRequest(content = content))

        Log.d("다이어리 내용 수정", "통신 상태 : Call 객체 초기화")

        updateDiaryResponseCall.enqueue(object : Callback<UpdateDiaryResponse> {
            override fun onResponse(call : Call<UpdateDiaryResponse>, response: Response<UpdateDiaryResponse>) {
                Log.d("다이어리 내용 수정", "통신 상태 : 성공")


                if (response.isSuccessful && response.body() != null) {

                    val result: UpdateDiaryResponse = response.body()!!
                    val resultCode: String = response.code().toString()


                    Log.d("다이어리 내용 수정", "결과 코드 : $resultCode")
                    val success: String = "200"
                    val badRequest: String = "300"
                    val internalServerError: String = "500"


                    if (resultCode == success) {
                        Toast.makeText(activity, "감사일기 수정이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            override fun onFailure(call: Call<UpdateDiaryResponse>, t: Throwable) {
                Log.d("다이어리 내용 수정", "통신 상태 : 실패")
                val string = t.message.toString()
                Log.d("다이어리 내용 수정", "예외 메세지 : $string")
                Log.d("다이어리 내용 수정", "요청 내용 : $updateDiaryResponseCall")
            }
        })
    }

    private fun deleteDiary(diaryPk : Long) {

        val sharedPref = requireActivity().getSharedPreferences("user_info", Context.MODE_PRIVATE)
        val userPk : Long = sharedPref.getLong("userPk", 1L)
        val accessToken : String = sharedPref.getString("accessToken", "").toString()

        Log.d("다이어리 내용 삭제", "통신 상태 : 변수 초기화")

        val deleteDiaryResponseCall : Call<DeleteDiaryResponse> = getNetworkService().deleteDiary(
            accessToken = accessToken, diaryPk = diaryPk, userPk = userPk
        )

        Log.d("다이어리 내용 삭제", "통신 상태 : Call 객체 생성")

        deleteDiaryResponseCall.enqueue(object : Callback<DeleteDiaryResponse> {
            override fun onResponse(call : Call<DeleteDiaryResponse>, response: Response<DeleteDiaryResponse>) {

                Log.d("다이어리 내용 삭제", "통신 상태 : 성공")

                if (response.isSuccessful && response.body() != null) {

                    val result: DeleteDiaryResponse = response.body()!!
                    val resultCode: String = response.code().toString()

                    Log.d("다이어리 내용 삭제", "결과 코드 : $resultCode")
                    val success: String = "200"
                    val badRequest: String = "300"
                    val internalServerError: String = "500"


                    if (resultCode == success) {

                        binding.diaryCalendarInsideFrg.setText("작성된 내용이 없습니다.")
                        Toast.makeText(activity, "감사일기가 성공적으로 삭제되었습니다.", Toast.LENGTH_SHORT).show()

                        //감사일기 삭제하면 캘린더 내부 내용이 완전히 안뜸.
                    }
                }
            }
            override fun onFailure(call: Call<DeleteDiaryResponse>, t: Throwable) {
                Log.d("다이어리 내용 삭제", "통신 상태 : 실패")
                val string = t.message.toString()
                Log.d("다이어리 내용 삭제", "예외 메세지 : $string")
                Log.d("다이어리 내용 삭제", "요청 내용 : $deleteDiaryResponseCall")
            }
        })
    }

/*    override fun onStop() {

        activity?.supportFragmentManager?.popBackStack("CalendarFragment",
            FragmentManager.POP_BACK_STACK_INCLUSIVE
        )

        super.onStop()
    }*/

}